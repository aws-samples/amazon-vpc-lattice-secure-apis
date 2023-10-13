include etc/environment.sh

# pre-requisite deploy dynamodb table
sam: sam.package sam.deploy
sam.package:
	sam package --profile ${PROFILE} -t ${SAM_TEMPLATE} --output-template-file ${SAM_OUTPUT} --s3-bucket ${BUCKET} --s3-prefix ${SAM_STACK}
sam.deploy:
	sam deploy --profile ${PROFILE} -t ${SAM_OUTPUT} --stack-name ${SAM_STACK} --parameter-overrides ${SAM_PARAMS} --capabilities CAPABILITY_NAMED_IAM

# build spring application
mvn.clean:
	mvn clean
mvn.compile:
	mvn compile
mvn.package:
	mvn package
mvn.run:
	./mvnw spring-boot:run

# containerize spring application
docker: docker.build docker.login docker.tag docker.push
docker.build:
	docker build -f dockerfile -t ${C_TAG} .
docker.login:
	aws ecr --profile ${PROFILE} get-login-password --region ${REGION} | docker login --username AWS --password-stdin ${C_REPO_BASE}
docker.tag:
	docker tag ${C_TAG} ${C_REPO_URI}
docker.push:
	docker push ${C_REPO_URI}
docker.run:
	docker run -p 8081:8080 --env-file etc/environment.docker ${C_TAG}
