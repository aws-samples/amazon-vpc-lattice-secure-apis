# Overview
This directory modifies the basic Spring Boot application to work inside Lambda with SnapStart enabled.

## Pre-requisites
Copy `etc/environment.template` to `etc/environment.sh` and update accordingly.
* `PROFILE`: your AWS CLI profile with the appropriate credentials to deploy
* `BUCKET`: your configuration bucket
* `P_NAME`: naming prefix for resources
* `P_STAGE`: stage for API Gateway
* `P_FN_MEMORY`: memory configuration in MB
* `P_FN_TIMEOUT`: timeout configuration in seconds
* `P_FN_ALIAS_NAME`: name to use for the function alias
* `P_FN_SNAPSTART_APPLY_ON`: `None` to disable SnapStart or `PublishedVersions` to enable it

For the infrastructure stack, update the following accordingly.
* `P_VPC_ID`: your VPC id
* `P_HOSTEDZONE_DOMAIN`: root domain name for the private hosted zone
* `P_DOMAINNAME`: domain name to be used for your certificate and alias for the load balancer.

For your hosted zone domain, you'll want to create a private domain off the public domain that you own. For example, heeki.cloud is the public hosted zone that is owned with this eample. Thus, `P_HOSTEDZONE_DOMAIN` is then configured as internal.heeki.cloud. For your domain name, you'll create an ACM certificate to be used as a custom domain name.

## Deployment
Deploy the infrastructure resources using `makefile`: `make infrastructure`

After completing the deployment, update the following outputs:
* `O_HOSTEDZONE_ID`: output hosted zone id
* `O_CERT_ARN`: output certificate ARN

Deploy the DynamoDB table: `make ddb`

After completing the deployment, update the following outputs:
* `O_TABLE_ARN`: output DynamoDB table ARN

Deploy the Lambda and API Gateway resources: `make lambda`

After completing the deployment, update the following outputs:
* `O_FN`: output Lambda function id
* `O_API_ENDPOINT`: output API Gateway endpoint URL, e.g. https://<api_id>.execute-api.<region>.amazonaws.com/<stage>

## Testing
To create a customer record and retrieve a list of all customer records:
```
mkdir tmp && cat > tmp/customer.json << EOF
{
    "given_name": "Jack",
    "family_name": "Doe",
    "birthdate": "1960-03-03",
    "email": "jack.doe@heeki.cloud",
    "phone_number": "+15551234567",
    "phone_number_verified": true
}
EOF
curl -s -XPOST -H "content-type:application/json" -d @tmp/customer.json https://<api_id>.execute-api.<region>.amazonaws.com/<stage>/customer
curl -s -XGET https://<api_id>.execute-api.<region>.amazonaws.com/<stage>customer
```
