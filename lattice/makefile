include etc/environment.sh

# lattice service network
network: network.package network.deploy
network.package:
	sam package --profile ${PROFILE} -t ${NETWORK_TEMPLATE} --output-template-file ${NETWORK_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${NETWORK_STACK}
network.deploy:
	sam deploy --profile ${PROFILE} -t ${NETWORK_OUTPUT} --stack-name ${NETWORK_STACK} --parameter-overrides ${NETWORK_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# vpc association (done in each account)
association: association.package association.deploy
association.package:
	sam package --profile ${PROFILE} -t ${NETWORK_VPC_ASSOCIATION_TEMPLATE} --output-template-file ${NETWORK_VPC_ASSOCIATION_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${NETWORK_VPC_ASSOCIATION_STACK}
association.deploy:
	sam deploy --profile ${PROFILE} -t ${NETWORK_VPC_ASSOCIATION_OUTPUT} --stack-name ${NETWORK_VPC_ASSOCIATION_STACK} --parameter-overrides ${NETWORK_VPC_ASSOCIATION_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# lattice service (done in each account where applications are deployed)
service: service.package service.deploy
service.package:
	sam package --profile ${PROFILE} -t ${SERVICE_TEMPLATE} --output-template-file ${SERVICE_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${SERVICE_STACK}
service.deploy:
	sam deploy --profile ${PROFILE} -t ${SERVICE_OUTPUT} --stack-name ${SERVICE_STACK} --parameter-overrides ${SERVICE_PARAMS} --capabilities CAPABILITY_NAMED_IAM
