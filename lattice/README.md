# Overview
This directory uses CloudFormation to setup VPC Lattice.

## Pre-requisites
Copy `etc/environment.template` to `etc/environment.sh` and update accordingly.
* `PROFILE`: your AWS CLI profile with the appropriate credentials to deploy
* `ACCOUNTID`: your AWS account id
* `REGION`: your AWS region
* `BUCKET`: your configuration bucket

For the service network stack, update the following accordingly.
* `P_VPC_ID`: your VPC id

## Deployment
Deploy using `makefile`: `make network`

The service network should be configured in a core infrastructure account and will be shared with other accounts using Resource Access Manager. Each of the accounts to which the service network is being shared needs to accept the invitation. Login to each account, go to Resource Access Manager, Resource shares, click on the invitation, and click "Accept resource share".

Configure the VPC association in each of the other accounts. You can configure `PROFILE` and `P_VPC_ID` for each account and run the following: `make association`

Configure the service in each of the other accounts. You can configure `PROFILE`, `P_DOMAINNAME`, `P_CERT_ARN`, `P_FN_ARN` and run the following: `make service` to deploy the service for Lambda.