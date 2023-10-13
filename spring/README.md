# Overview
This directory has a basic Spring Boot application that exposes an API and uses DynamoDB as the backend.

## Pre-requisites
Copy `etc/environment.template` to `etc/environment.sh` and update accordingly.
* `PROFILE`: your AWS CLI profile with the appropriate credentials to deploy
* `ACCOUNTID`: your AWS account id
* `REGION`: your AWS region
* `BUCKET`: your configuration bucket
* `P_NAME`: naming prefix for resources

## Deployment
Deploy the DynamoDB table and ECR repository using `makefile`: `make sam`

After completing the deployment, update the following outputs:
* `O_TABLE_ARN`: output with the DynamoDB table ARN
* `O_ECR_REPO`: output with the ECR repository name

Build the container image locally and push it to the ECR repository: `make docker`

## Testing
To run the Spring Boot application directly on your local machine: `make mvn.run`
```
2023-09-12 20:27:38.770  INFO 89720 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2023-09-12 20:27:38.822  INFO 89720 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2023-09-12 20:27:38.842  INFO 89720 --- [           main] cloud.heeki.DemoApplication              : Started DemoApplication in 2.748 seconds (JVM running for 3.094)
```

To create a customer record and retrieve a list of all customer records:
```
mkdir tmp && cat > tmp/customer.json << EOF
{
    "given_name": "Jack",
    "family_name": "Doe",
    "birthdate": "1960-03-03",
    "email": "jack.doe@heeki.cloud",
    "phone_number": "+15551234567",
    "phone_number_verified": true
}
EOF
curl -s -XPOST -H "content-type:application/json" -d @tmp/customer.json http://localhost:8080/customer
curl -s -XGET http://localhost:8080/customer
```

To run the Spring Boot application via a docker image on your local machine, you'll first need to setup `etc/environment.docker` as follows:
```
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-access-key
TABLE=your-dynamodb-table-name
```

The credentials are required to ensure that your application has access to read/write from/to the DynamoDB table. Once that is setup, you can run the following:
```
make docker.build
make docker.run
curl -s -XPOST -H "content-type:application/json" -d @tmp/customer.json http://localhost:8081/customer
curl -s -XGET http://localhost:8081/customer
```

Notice that we update the target port from 8080 (native to the application) to 8081 (container port mapping).