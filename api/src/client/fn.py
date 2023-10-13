import json
import os
import requests
from aws_xray_sdk.core import xray_recorder
from aws_xray_sdk.core import patch_all

import botocore.session
from botocore.auth import SigV4Auth
from botocore.awsrequest import AWSRequest
from botocore.credentials import Credentials

# initialization: environment variables
region = os.environ.get("AWS_REGION", "us-east-1")
lattice_endpoint = os.environ.get("LATTICE_SERVICE_ENDPOINT", "undefined")

# initialization: boto
session = botocore.session.get_session()

# initialization: xray
patch_all()

# helper functions
def build_response(code, body):
    # headers for cors
    headers = {
        # "Access-Control-Allow-Origin": "amazonaws.com",
        # "Access-Control-Allow-Credentials": True,
        "Content-Type": "application/json"
    }
    # lambda proxy integration
    response = {
        "isBase64Encoded": False,
        "statusCode": code,
        "headers": headers,
        "body": body
    }
    return response

def parse_flag(event, flag):
    response = False
    if flag in event and event[flag]:
        response = True
    return response

def send_request(event, add_sigv4=False, debug=False):
    headers = {
        # "x-amz-content-sha256": "UNSIGNED-PAYLOAD",
        "content-type": "application/json"
    }

    endpoint = f"https://{lattice_endpoint}?key=value" if "endpoint" not in event else event["endpoint"]
    method = "GET" if "method" not in event else event["method"]
    data = json.dumps({}) if "data" not in event else json.dumps(event["data"])
    request = AWSRequest(method=method, url=endpoint, data=data, headers=headers)
    request.context["payload_signing_enabled"] = False

    if add_sigv4:
        print(json.dumps({
            "message": "sigv4 signing the request"
        })) if debug else None
        sigv4 = SigV4Auth(session.get_credentials(), "vpc-lattice-svcs", region)
        sigv4.add_auth(request)

    timeout = 5
    output = {}
    try:
        print(json.dumps({
            "endpoint": endpoint
        })) if debug else None
        prepped = request.prepare()
        # throws requests.exceptions.ReadTimeout, requests.exceptions.ConnectionError
        if method == "POST":
            response = requests.post(prepped.url, headers=prepped.headers, data=data, timeout=timeout)
        elif method == "DELETE":
            response = requests.delete(prepped.url, headers=prepped.headers, timeout=timeout)
        else:
            response = requests.get(prepped.url, headers=prepped.headers, timeout=timeout)

        # response is of type requests.models.Response
        if response.status_code == 200:
            # throws requests.exceptions.JSONDecodeError
            output = response.json()
        else:
            output = {
                "status_code": response.status_code,
                "reason": response.reason
            }
    except requests.exceptions.ReadTimeout:
        output = {
            "status_code": 504,
            "reason": f"request to vpc lattice backend timed out ({timeout} seconds)"
        }
    except (requests.exceptions.ConnectionError):
        output = {
            "status_code": 504,
            "reason": "connection reset by peer and aborted"
        }
    except (requests.exceptions.JSONDecodeError):
        output = {
            "status_code": 200,
            "reason": "no json returned in the response",
        }

    return output

def handler(event, context):
    print(json.dumps(event))
    body = json.loads(event["body"])

    enable_sigv4 = parse_flag(body, "sigv4")
    enable_debug = parse_flag(body, "debug")
    print(json.dumps({
        "enable_sigv4": enable_sigv4,
        "enable_debug": enable_debug
    }))
    output = send_request(body, add_sigv4=enable_sigv4, debug=enable_debug)
    print(json.dumps(output))

    response = build_response(200, json.dumps(output))
    print(json.dumps(response))
    return response
