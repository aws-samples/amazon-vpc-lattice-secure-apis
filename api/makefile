include etc/environment.sh

# infrastructure for vpc lattice
infrastructure: infrastructure.package infrastructure.deploy
infrastructure.package:
	sam package --profile ${PROFILE} -t ${INFRASTRUCTURE_TEMPLATE} --output-template-file ${INFRASTRUCTURE_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${INFRASTRUCTURE_STACK}
infrastructure.deploy:
	sam deploy --profile ${PROFILE} -t ${INFRASTRUCTURE_OUTPUT} --stack-name ${INFRASTRUCTURE_STACK} --parameter-overrides ${INFRASTRUCTURE_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# cognito
cognito: cognito.package cognito.deploy
cognito.package:
	sam package --profile ${PROFILE} -t ${COGNITO_TEMPLATE} --output-template-file ${COGNITO_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${COGNITO_STACK}
cognito.deploy:
	sam deploy --profile ${PROFILE} -t ${COGNITO_OUTPUT} --region ${REGION} --stack-name ${COGNITO_STACK} --parameter-overrides ${COGNITO_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# cognito: initialize a new user
cognito.admin: cognito.admin.1 cognito.admin.2
cognito.admin.1:
	aws cognito-idp admin-initiate-auth --profile ${PROFILE} --user-pool-id ${O_USERPOOL_ID} --client-id ${O_USERPOOL_CLIENTID} --auth-flow ADMIN_NO_SRP_AUTH --auth-parameters USERNAME=${P_USER_NAME},PASSWORD=${P_COGNITO_USERTEMPPW} | tee tmp/cognito_admin_1.json
cognito.admin.2:
	aws cognito-idp admin-respond-to-auth-challenge --profile ${PROFILE} --user-pool-id ${O_USERPOOL_ID} --client-id ${O_USERPOOL_CLIENTID} --challenge-name NEW_PASSWORD_REQUIRED --challenge-responses USERNAME=${P_USER_NAME},NEW_PASSWORD=${P_COGNITO_USERPERMPW} --session ${O_COGNITO_SESSION} | tee tmp/cognito_admin_2.json
# cognito: get fresh credentials for user
cognito.user:
	aws cognito-idp initiate-auth --profile ${PROFILE} --client-id ${O_USERPOOL_CLIENTID} --auth-flow USER_PASSWORD_AUTH --auth-parameters USERNAME=${P_USER_NAME},PASSWORD=${P_COGNITO_USERPERMPW} | tee tmp/cognito_user.json

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

# test api with auth
api.auth:
	curl -s -XPOST -H "content-type: application/x-amz-json-1.1" -H "x-amz-target: AWSCognitoIdentityProviderService.InitiateAuth" -d @etc/auth.json https://cognito-idp.${REGION}.amazonaws.com | jq > tmp/cognito_user.json
api.curl:
	curl -s -XPOST -H "Authorization: ${O_COGNITO_ID_TOKEN}" -d @etc/event_lattice.json ${O_API_ENDPOINT} | jq
api.invoke: api.auth api.curl

# route 53
route53: route53.package route53.deploy
route53.package:
	sam package --profile ${PROFILE} -t ${R53_TEMPLATE} --output-template-file ${R53_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${R53_STACK}
route53.deploy:
	sam deploy --profile ${PROFILE} -t ${R53_OUTPUT} --region ${REGION} --stack-name ${R53_STACK} --parameter-overrides ${R53_PARAMS} --capabilities CAPABILITY_NAMED_IAM
