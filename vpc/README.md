# Overview
This directory uses CDK to setup a VPC with two public subnets and two private subnets in your AWS account.

## Pre-requisites
If deploying to a new account without CDK initialized, be sure to bootstrap first: `cdk bootstrap --profile your-cli-profile aws://<your-aws-account-id>/<your-aws-region>`

Copy `etc/environment.template` to `etc/environment.sh` and update accordingly.
* `PROFILE`: your AWS CLI profile with the appropriate credentials to deploy
* `P_CIDR`: the CIDR block that you want to configure with the VPC

## Deployment
Deploy using `makefile`: `make cdk`
