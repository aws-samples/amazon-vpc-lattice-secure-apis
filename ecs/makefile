include etc/environment.sh

# infrastructure for vpc lattice
infrastructure: infrastructure.package infrastructure.deploy
infrastructure.package:
	sam package --profile ${PROFILE} -t ${INFRASTRUCTURE_TEMPLATE} --output-template-file ${INFRASTRUCTURE_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${INFRASTRUCTURE_STACK}
infrastructure.deploy:
	sam deploy --profile ${PROFILE} -t ${INFRASTRUCTURE_OUTPUT} --stack-name ${INFRASTRUCTURE_STACK} --parameter-overrides ${INFRASTRUCTURE_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# ecs cluster and service
ecs: ecs.package ecs.deploy
ecs.package:
	sam package --profile ${PROFILE} -t ${ECS_TEMPLATE} --output-template-file ${ECS_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${ECS_STACK}
ecs.deploy:
	sam deploy --profile ${PROFILE} -t ${ECS_OUTPUT} --stack-name ${ECS_STACK} --parameter-overrides ${ECS_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# bastion host for testing private endpoints
bastion: bastion.package bastion.deploy
bastion.package:
	sam package --profile ${PROFILE} -t ${EC2_TEMPLATE} --output-template-file ${EC2_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${EC2_STACK}
bastion.deploy:
	sam deploy --profile ${PROFILE} -t ${EC2_OUTPUT} --stack-name ${EC2_STACK} --parameter-overrides ${EC2_PARAMS} --capabilities CAPABILITY_NAMED_IAM
