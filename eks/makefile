include etc/environment.sh

# infrastructure for vpc lattice
infrastructure: infrastructure.package infrastructure.deploy
infrastructure.package:
	sam package --profile ${PROFILE} -t ${INFRASTRUCTURE_TEMPLATE} --output-template-file ${INFRASTRUCTURE_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${INFRASTRUCTURE_STACK}
infrastructure.deploy:
	sam deploy --profile ${PROFILE} -t ${INFRASTRUCTURE_OUTPUT} --stack-name ${INFRASTRUCTURE_STACK} --parameter-overrides ${INFRASTRUCTURE_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# create eks cluster
cluster.create:
	eksctl create cluster --profile ${PROFILE} -f iac/kubectl/cluster.yaml
cluster.delete:
	eksctl delete cluster --profile ${PROFILE} -f iac/kubectl/cluster.yaml

# create service account iam role using sam
# iam: iam.package iam.deploy
# iam.package:
# 	sam package -t ${IAM_TEMPLATE} --output-template-file ${IAM_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${IAM_STACK}
# iam.deploy:
# 	sam deploy -t ${IAM_OUTPUT} --stack-name ${IAM_STACK} --parameter-overrides ${IAM_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# create service account iam role using cloudformation
sa.create:
	aws cloudformation create-stack --profile ${PROFILE} --stack-name ${IAM_STACK} --template-body file://${IAM_TEMPLATE} --parameters file://${IAM_PARAMS} --capabilities CAPABILITY_IAM | jq
sa.update:
	aws cloudformation update-stack --profile ${PROFILE} --stack-name ${IAM_STACK} --template-body file://${IAM_TEMPLATE} --parameters file://${IAM_PARAMS} --capabilities CAPABILITY_IAM | jq

# create gateway api iam role using cloudformation
gapi.create:
	aws cloudformation create-stack --profile ${PROFILE} --stack-name ${GAPI_STACK} --template-body file://${GAPI_TEMPLATE} --parameters file://${GAPI_PARAMS} --capabilities CAPABILITY_IAM | jq
gapi.update:
	aws cloudformation update-stack --profile ${PROFILE} --stack-name ${GAPI_STACK} --template-body file://${GAPI_TEMPLATE} --parameters file://${GAPI_PARAMS} --capabilities CAPABILITY_IAM | jq

# create k8s service account and annotate it with the iam role
sa.create.application:
	kubectl create sa ${P_APPLICATION_SERVICE_ACCOUNT} -n ${P_APPLICATION_NAMESPACE}
	kubectl annotate sa ${P_APPLICATION_SERVICE_ACCOUNT} eks.amazonaws.com/role-arn=${O_APPLICATION_ROLE_ARN} -n ${P_APPLICATION_NAMESPACE}
sa.create.gatewayapi:
	kubectl create sa ${P_GATEWAYAPI_SERVICE_ACCOUNT} -n ${P_GATEWAYAPI_NAMESPACE}
	kubectl annotate sa ${P_GATEWAYAPI_SERVICE_ACCOUNT} eks.amazonaws.com/role-arn=${O_GATEWAYAPI_ROLE_ARN} -n ${P_GATEWAYAPI_NAMESPACE}
sa.list:
	kubectl get sa -A | egrep "NAME|${P_APPLICATION_SERVICE_ACCOUNT}|${P_GATEWAYAPI_SERVICE_ACCOUNT}"

# create k8s gateway api controller
gapi.namesystem:
	kubectl apply -f iac/kubectl/deploy-namesystem.yaml
	kubectl get ns -A
gapi.controller:
	kubectl apply -f iac/kubectl/deploy-v0.0.16.yaml
gapi.class:
	kubectl apply -f iac/kubectl/gateway-class.yaml

# create k8s gateway
gapi.gateway:
	kubectl apply -f iac/kubectl/gateway.yaml
	kubectl get gateway -A
gapi.routes:
	kubectl apply -f iac/kubectl/gateway-routes.yaml
	kubectl get httproute -A

# update eks deployments with new images
eks.update.image:
	kubectl set image deployment/${P_DEPLOYMENT} ${P_CONTAINER}=${C_REPO_URI} -n ${P_APPLICATION_NAMESPACE}
	kubectl get rs -n ${P_APPLICATION_NAMESPACE} -o yaml | egrep "image:|generation:"
	kubectl get pods -n ${P_APPLICATION_NAMESPACE}
eks.shell:
	kubectl get endpoints
	kubectl run client --rm -it --namespace=${P_APPLICATION_NAMESPACE} --image=alpine/curl -- sh
