# Overview
This directory has a Spring Boot application that is deployed as an ECS service behind an ALB.

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
* `P_INGRESS_CIDR`: ingress CIDR range for a client security group
* `P_SUBNETIDS_PRIVATE`: comma-separated list of private subnet ids

For your hosted zone domain, you'll want to create a private domain off the public domain that you own. For example, heeki.cloud is the public hosted zone that is owned with this eample. Thus, `P_HOSTEDZONE_DOMAIN` is then configured as internal.heeki.cloud. For your domain name, you'll create an ACM certificate, which will be applied to the load balancer, and an alias in Route 53 to point the domain name to the load balancer FQDN.

For the ECS stack, update the following accordingly.
* `P_DESIRED_COUNT`: number of ECS tasks to be configured behind the load balancer
* `P_TABLE_ARN`: ARN for the DynamoDB that backed the Spring Boot application

For the optional bastion to test your private ECS application, update the following accordingly.
* `P_SUBNETIDS_PUBLIC`: comma-separated list of public subnet ids
* `P_BASTION_INGRESS_CIDR`: ingress CIDR range for your bastion host
* `P_IMAGE_ID`: AMI to be used for the EC2 instance
* `P_INSTANCE_TYPE`: instance type
* `P_SSH_KEY`: EC2 key to allow you to SSH into the bastion host

If you have a brand new account, you will also need to ensure that the ECS service-linked role exists. Refer to the [documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/using-service-linked-roles.html) for creating it.

## Deployment
Deploy the infrastructure resources using `makefile`: `make infrastructure`

After completing the deployment, update the following outputs:
* `O_HOSTEDZONE_ID`: output Route 53 hosted zone id
* `O_CERT_ARN`: output ACM certificate ARN
* `O_ALB_ARN`: output ALB ARN
* `O_ALB_TGROUP`:  output ALB target group ARN
* `O_SGROUP_CLIENT`: output client security group ARN
* `O_SGROUP_ALB`: output ALB security group ARN
* `O_SGROUP_TASK`: output task security group ARN

Deploy the ECS cluster, task, and service: `make ecs`

Deploy the optional bastion host: `make bastion`

## Testing
If you deployed the optional bastion, ssh to the host: `ssh -i <your-ec2-key>.pem ec2-user@<your-bastion-fqdn>`

To create a customer record and retrieve a list of all customer records:
```
mkdir tmp && cat > customer.json << EOF
{
    "given_name": "Jack",
    "family_name": "Doe",
    "birthdate": "1960-03-03",
    "email": "jack.doe@heeki.cloud",
    "phone_number": "+15551234567",
    "phone_number_verified": true
}
EOF
curl -s -XPOST -H "content-type:application/json" -d @customer.json https://<your-domain-name>/customer
curl -s -XGET https://<your-domain-name>/customer
```
