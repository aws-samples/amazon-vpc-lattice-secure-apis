# Overview
This directory implements a north-south API frontend for communication into the east-west VPC Lattice service network.

## Pre-requisites
Copy `etc/environment.template` to `etc/environment.sh` and update accordingly.
* `PROFILE`: your AWS CLI profile with the appropriate credentials to deploy
* `ACCOUNTID`: your AWS account id
* `REGION`: your AWS region
* `BUCKET`: your configuration bucket

For the infrastructure stack, update the following accordingly.
* `P_VPC_ID`: your VPC id
* `P_HOSTEDZONE_DOMAIN`: root domain name for the private hosted zone
* `P_DOMAINNAME`: domain name to be used for your certificate and alias for the load balancer.

For the Cognito stack, update the following accordingly.
* `P_USERPOOL_DOMAIN`: the custom domain name used in https://<domain>.auth.<region>.amazoncognito.com
* `P_USER_NAME`: sample user name
* `P_USER_EMAIL`: associated email address, to which a temporary password will be emailed

For the Lambda and API Gateway stack, update the following accordingly.
* `P_STAGE`: stage name for API Gateway
* `P_FN_MEMORY`: amount of memory in MB for the Lambda function
* `P_FN_TIMEOUT`: timeout in seconds for the Lambda function

## Deployment
Deploy the infrastructure resources using `makefile`: `make infrastructure`

After completing the deployment, update the following outputs:
* `O_HOSTEDZONE_ID`: output hosted zone id
* `O_CERT_ARN`: output certificate ARN

Deploy the Cognito resources: `make cognito`

After completing the deployment, update the following outputs:
* `O_USERPOOL_ID`: output user pool id
* `O_USERPOOL_PROVIDERNAME`: output provider name
* `O_USERPOOL_PROVIDERURL`: output provider endpoint url
* `O_USERPOOL_CLIENTID`: output client id, used for getting a jwt token
* `O_USERPOOL_DOMAIN`: output confirming the user pool domain
* `O_IDENTITYPOOL_ID`: output identity pool id

Deploy the Lambda and API Gateway resources: `make lambda`

After completing the deployment, update the following outputs:
* `O_FN`: output Lambda function name
* `O_API_ENDPOINT`: output API Gateway endpoint URL, e.g. https://<api_id>.execute-api.<region>.amazonaws.com/<stage>
* `O_LAYER_ARN`: output layer ARN
* `O_SGROUP`: output security group id

## Testing
To setup the user that you created, update the following parameters:
* `P_COGNITO_USERTEMPPW`: temporary password that was emailed to the email address that you entered
* `P_COGNITO_USERPERMPW`: permanent password that you select and is configured for your user

Update the temporary password to a permanent password and get a fresh set of credentials: `make cognito.admin`

Once updated with a permanent password, you can subsequently get a fresh set of credentials using the CLI: `make cognito.user`

Alternatively, you can get a fresh set of credentials making an API request: `make api.auth`

Test the API endpoint with the JWT token that you get from Cognito: `make api.invoke`
