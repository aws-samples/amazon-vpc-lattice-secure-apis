# Overview
This directory sets up the EKS cluster, configures a service account for IRSA, and deploys a Spring Boot application.

## Pre-requisites
Copy `etc/environment.template` to `etc/environment.sh` and update accordingly.
* `PROFILE`: your AWS CLI profile with the appropriate credentials to deploy
* `ACCOUNTID`: your AWS account id
* `REGION`: your AWS region
* `BUCKET`: your configuration bucket
* `P_OIDC_PROVIDER`: OpenID Connect provider URL, e.g. oidc.eks.us-east-1.amazonaws.com/id/01234567890ABCDEFGHIJKLMNOPQRSTU
* `P_NAMESPACE`: your namespace, e.g. spring
* `P_CONTAINER`: your container name, e.g. spring-container
* `P_SERVICE_ACCOUNT`: your service account name, e.g. spring-serviceaccount
* `P_DEPLOYMENT`: your deployment name, e.g. spring-deployment
* `P_TABLE_ARN`: your DynamoDB table ARN

For the infrastructure stack, update the following accordingly.
* `P_VPC_ID`: your VPC id
* `P_HOSTEDZONE_DOMAIN`: root domain name for the private hosted zone
* `P_DOMAINNAME`: domain name to be used for your certificate and alias for the load balancer.

For your hosted zone domain, you'll want to create a private domain off the public domain that you own. For example, heeki.cloud is the public hosted zone that is owned with this eample. Thus, `P_HOSTEDZONE_DOMAIN` is then configured as internal.heeki.cloud. For your domain name, you'll create an ACM certificate to be used as a custom domain name.

## Deployment
Create the EKS cluster using `eksctl`: `eksctl create cluster --profile your-aws-profile -f iac/cluster.yaml`

Create an IAM role to be used for the k8s service account: `make iam`

After completing the deployment, update the following outputs:
* `O_ROLE_ARN`: output with IAM role ARN

Create a namespace: `kubectl create namespace spring`

Create the k8s service account and associate it to the IAM role: `make sa.create`

Create a deployment: `kubectl apply -f iac/deployment.yaml -n spring`

Create a service: `kubectl apply -f iac/service.yaml -n spring`

View all the resources: `kubectl get all -n spring`

## Testing
To test the API:
```
make eks.shell
cat > /tmp/customer.json << EOF
{
    "given_name": "Jack",
    "family_name": "Doe",
    "birthdate": "1960-03-03",
    "email": "jack.doe@heeki.cloud",
    "phone_number": "+15551234567",
    "phone_number_verified": true
}
EOF
curl -s -XPOST -H "content-type:application/json" -d @/tmp/customer.json http://spring-service/customer
curl -s -XGET http://spring-service/customer
```

## Troubleshooting
If your pods are not starting and have a `CrashLoopBackOff` status, start by looking at the logs of one of the pods: 
```
kubectl get pods -n spring
kubectl logs <pod>
```

If you need to update the pod image, update the image as follows: `make eks.update.image`

In the command above, you will set the image for the existing deployment, which is crashing, rather than performing a new deployment, as you might when using CloudFormation or Terraform. If you were to do another `kubectl apply -f iac/deployment.yaml -n spring`, k8s would queue up the deployment behind the current deployment which is failing and stuck.
