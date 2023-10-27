PROFILE=your-aws-profile
ACCOUNTID=your-aws-account-id
REGION=your-aws-region
BUCKET=your-configuration-bucket

P_VPC_ID=your-vpc-id
P_HOSTEDZONE_DOMAIN=internal.heeki.cloud
P_DOMAINNAME=api.internal.heeki.cloud
INFRASTRUCTURE_STACK=lambda-core-infrastructure
INFRASTRUCTURE_TEMPLATE=iac/infrastructure.yaml
INFRASTRUCTURE_OUTPUT=iac/infrastructure_output.yaml
INFRASTRUCTURE_PARAMS="ParameterKey=pVpcId,ParameterValue=${P_VPC_ID} ParameterKey=pHostedZoneDomain,ParameterValue=${P_HOSTEDZONE_DOMAIN} ParameterKey=pDomainName,ParameterValue=${P_DOMAINNAME} ParameterKey=pIngressCidr,ParameterValue=${P_INGRESS_CIDR} ParameterKey=pSubnetIds,ParameterValue=${P_SUBNETIDS_PRIVATE}"
O_HOSTEDZONE_ID=output-hostedzone-id
O_CERT_ARN=O_CERT_ARN=output-certificate-arn

P_USERPOOL_DOMAIN=lattice-api-authentication
P_USER_NAME=your-user-name
P_USER_EMAIL=your-user-email
COGNITO_STACK=lambda-api-cognito
COGNITO_TEMPLATE=iac/cognito.yaml
COGNITO_OUTPUT=iac/cognito_output.yaml
COGNITO_PARAMS="ParameterKey=pUserPoolDomain,ParameterValue=${P_USERPOOL_DOMAIN} ParameterKey=pUserName,ParameterValue=${P_USER_NAME} ParameterKey=pUserEmail,ParameterValue=${P_USER_EMAIL}"
O_USERPOOL_ID=output-userpool-id
O_USERPOOL_PROVIDERNAME=output-userpool-providername
O_USERPOOL_PROVIDERURL=output-userpool-providerurl
O_USERPOOL_CLIENTID=output-userpool-clientid
O_USERPOOL_DOMAIN=output-userpool-domain
O_IDENTITYPOOL_ID=output-identitypool-id
P_USERPOOL_CLIENTID=$(shell echo ${O_USERPOOL_CLIENTID} | openssl base64)

P_COGNITO_USERTEMPPW=your-temporary-password-that-was-emailed-to-you
P_COGNITO_USERPERMPW=your-new-secure-permanent-password
O_COGNITO_SESSION=$(shell cat tmp/cognito_admin_1.json | jq -r ".Session")
O_COGNITO_ID_TOKEN=$(shell cat tmp/cognito_user.json | jq -r ".AuthenticationResult.IdToken")
O_COGNITO_ACCESS_TOKEN=$(shell cat tmp/cognito_user.json | jq -r ".AuthenticationResult.AccessToken")

P_STAGE=dev
P_FN_MEMORY=128
P_FN_TIMEOUT=60
P_SERVICE_ENDPOINT=your-vpc-lattice-service-endpoint-fqdn
P_SERVICE_ARNS=your-vpc-lattice-service-arn/*
P_SUBNETIDS_PRIVATE=your-comma-separated-private-subnetids
LAMBDA_STACK=lambda-api-client
LAMBDA_TEMPLATE=iac/lambda.yaml
LAMBDA_OUTPUT=iac/lambda_output.yaml
LAMBDA_PARAMS="ParameterKey=pApiStage,ParameterValue=${P_STAGE} ParameterKey=pUserPoolId,ParameterValue=${O_USERPOOL_ID} ParameterKey=pFnMemory,ParameterValue=${P_FN_MEMORY} ParameterKey=pFnTimeout,ParameterValue=${P_FN_TIMEOUT} ParameterKey=pServiceEndpoint,ParameterValue=${P_SERVICE_ENDPOINT} ParameterKey=pServiceArns,ParameterValue=${P_SERVICE_ARNS} ParameterKey=pVpcId,ParameterValue=${P_VPC_ID} ParameterKey=pSubnetIds,ParameterValue=${P_SUBNETIDS_PRIVATE}"
O_API_ENDPOINT=output-api-endpoint
O_FN=output-fn-id
O_LAYER_ARN=output-layer-version-arn
O_SGROUP=output-sgroup-id

P_SUBNETIDS_PUBLIC=your-comma-separated-public-subnetids
P_BASTION_INGRESS_CIDR=your-ingress-cidr
P_IMAGE_ID=ami-04cb4ca688797756f
P_INSTANCE_TYPE=t2.micro
P_SSH_KEY=your-ec2-ssh-keypair-name
EC2_STACK=lambda-api-bastion
EC2_TEMPLATE=iac/bastion.yaml
EC2_OUTPUT=iac/bastion_output.yaml
EC2_PARAMS="ParameterKey=pVpcId,ParameterValue=${P_VPC_ID} ParameterKey=pSubnetIds,ParameterValue=${P_SUBNETIDS_PUBLIC} ParameterKey=pIngressCidr,ParameterValue=${P_BASTION_INGRESS_CIDR} ParameterKey=pClientSGroup,ParameterValue=${O_SGROUP_CLIENT} ParameterKey=pImageId,ParameterValue=${P_IMAGE_ID} ParameterKey=pInstanceType,ParameterValue=${P_INSTANCE_TYPE} ParameterKey=pKey,ParameterValue=${P_SSH_KEY}"
O_PUBLIC_DNS=output-ec2-public-dns-fqdn
O_PUBLIC_IP=output-ec2-public-ip

P_R53_HOSTEDZONE_ID=your-hostedzone-id
P_R53_CNAME=eks.internal.heeki.cloud
P_R53_LATTICE_ENDPOINT=your-lattice-service-endpoint-fqdn
R53_STACK=lambda-core-alias-eks
R53_TEMPLATE=iac/route53.yaml
R53_OUTPUT=iac/route53_output.yaml
R53_PARAMS="ParameterKey=pHostedZoneId,ParameterValue=${P_R53_HOSTEDZONE_ID} ParameterKey=pDomainName,ParameterValue=${P_R53_CNAME} ParameterKey=pEndpoint,ParameterValue=${P_R53_LATTICE_ENDPOINT}"
