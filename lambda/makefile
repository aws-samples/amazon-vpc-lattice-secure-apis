include etc/environment.sh

# build spring application
mvn.clean:
	mvn clean
mvn.compile:
	mvn compile
mvn.package:
	mvn package
mvn.run:
	./mvnw spring-boot:run

# infrastructure for vpc lattice
infrastructure: infrastructure.package infrastructure.deploy
infrastructure.package:
	sam package --profile ${PROFILE} -t ${INFRASTRUCTURE_TEMPLATE} --output-template-file ${INFRASTRUCTURE_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${INFRASTRUCTURE_STACK}
infrastructure.deploy:
	sam deploy --profile ${PROFILE} -t ${INFRASTRUCTURE_OUTPUT} --stack-name ${INFRASTRUCTURE_STACK} --parameter-overrides ${INFRASTRUCTURE_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# dynamodb table
ddb: ddb.package ddb.deploy
ddb.package:
	sam package --profile ${PROFILE} -t ${DDB_TEMPLATE} --output-template-file ${DDB_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${DDB_STACK}
ddb.deploy:
	sam deploy --profile ${PROFILE} -t ${DDB_OUTPUT} --stack-name ${DDB_STACK} --parameter-overrides ${DDB_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# prep layers
layer:
	pip install -r src/layer/requirements.txt --target=tmp/layer/python --upgrade

# lambda and api gateway
lambda: lambda.package lambda.deploy
lambda.package:
	sam package --profile ${PROFILE} -t ${LAMBDA_TEMPLATE} --output-template-file ${LAMBDA_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${LAMBDA_STACK}
lambda.deploy:
	sam deploy --profile ${PROFILE} -t ${LAMBDA_OUTPUT} --region ${REGION} --stack-name ${LAMBDA_STACK} --parameter-overrides ${LAMBDA_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# test locally and invoke deployed function
lambda.local:
	sam local invoke -t ${LAMBDA_TEMPLATE} --parameter-overrides ${LAMBDA_PARAMS} --env-vars etc/envvars.json -e etc/event.json Fn | jq -r '.body' | jq
lambda.invoke.sync:
	aws lambda invoke --profile ${PROFILE} --function-name ${O_FN} --invocation-type RequestResponse --payload file://etc/event.json --cli-binary-format raw-in-base64-out --log-type Tail tmp/fn.json | jq "." > tmp/response.json
	cat tmp/response.json | jq -r ".LogResult" | base64 --decode
	cat tmp/fn.json | jq
lambda.invoke.async:
	aws lambda invoke --profile ${PROFILE} --function-name ${O_FN} --invocation-type Event --payload file://etc/event.json --cli-binary-format raw-in-base64-out --log-type Tail tmp/fn.json | jq "."

# permissions to allow lattice to invoke lambda
perms: perms.package perms.deploy
perms.package:
	sam package --profile ${PROFILE} -t ${PERMS_TEMPLATE} --output-template-file ${PERMS_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${PERMS_STACK}
perms.deploy:
	sam deploy --profile ${PROFILE} -t ${PERMS_OUTPUT} --stack-name ${PERMS_STACK} --parameter-overrides ${PERMS_PARAMS} --capabilities CAPABILITY_NAMED_IAM
