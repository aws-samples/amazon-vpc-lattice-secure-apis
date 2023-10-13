package cloud.heeki.lib;

import com.google.gson.Gson;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.paginators.ScanIterable;
import java.nio.file.Files;

public class DynamoAdapter {
    private AwsCredentialsProvider credentials;
    private Region region;
    private DynamoDbClient client;
    private String table;
    private Gson g;

    public DynamoAdapter(String table) {
        // WebIdentityTokenFileCredentialsProvider doesn't expose a method to set the HTTP client, so need to set this at the system level
        System.setProperty("software.amazon.awssdk.http.service.impl", "software.amazon.awssdk.http.urlconnection.UrlConnectionSdkHttpService");

        String awsProfile = System.getenv("AWS_PROFILE");
        String awsRegion = System.getenv("AWS_REGION");
        String awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        String awsRoleArn = System.getenv("AWS_ROLE_ARN");
        String awsWebIdentityToken = System.getenv("AWS_WEB_IDENTITY_TOKEN_FILE");
        String awsContainerCredentialsFull = System.getenv("AWS_CONTAINER_CREDENTIALS_RELATIVE_URI");
        String awsContainerCredentialsRelative = System.getenv("AWS_CONTAINER_CREDENTIALS_FULL_URI");
        String awsContainerAuthToken = System.getenv("AWS_CONTAINER_AUTHORIZATION_TOKEN");

        this.g = new Gson();
        // when credentials are available via environment variables
        if (awsAccessKey != null && awsSecretKey != null) {
            this.credentials = EnvironmentVariableCredentialsProvider.create();
        // when operating in EKS
        } else if (awsRoleArn != null && awsWebIdentityToken != null) {
            this.credentials = WebIdentityTokenFileCredentialsProvider.create();
        // when operating in ECS
        } else if (awsContainerCredentialsFull != null) {
            this.credentials = ContainerCredentialsProvider.builder().build();
        // when using SnapStart since EnvironmentVariableCredentialsProvider is not supported
        } else if (awsContainerCredentialsRelative != null && awsContainerAuthToken != null) {
            this.credentials = ContainerCredentialsProvider.builder().build();
        // when operating locally with a cli profile
        } else {
            this.credentials = ProfileCredentialsProvider.create();
        }
        this.table = table;
        this.region = (awsRegion != null) ? Region.of(awsRegion) : Region.US_EAST_1;
        this.client = DynamoDbClient.builder()
            .credentialsProvider(credentials)
            .region(region)
            .httpClient(UrlConnectionHttpClient.builder().build())
            .build();
    }

    public ScanIterable scan() {
        ScanRequest request = ScanRequest.builder()
            .tableName(this.table)
            .build();
        ScanIterable response = client.scanPaginator(request);
        return response;
    }

    public String put(Customer c) {
        PutItemRequest request = PutItemRequest.builder()
            .tableName(this.table)
            .item(c.toDynamoMap())
            .build();
        Map<String, String> payload = new HashMap<>();
        payload.put("table", this.table);
        try {
            PutItemResponse response = client.putItem(request);
            payload.put("request_id", response.responseMetadata().requestId());
            System.out.println(this.g.toJson(payload));
        } catch (ResourceNotFoundException e) {
            payload.put("error", "table not found");
            System.err.println(this.g.toJson(payload));
            System.exit(1);
        } catch (DynamoDbException e) {
            payload.put("error", e.getMessage());
            System.err.println(this.g.toJson(payload));
            System.exit(1);
        }
        return this.g.toJson(payload);
    }

    public String delete(String uuid) {
        Map<String, AttributeValue> keymap = new HashMap<>();
        keymap.put("uuid", AttributeValue.builder().s(uuid).build());
        DeleteItemRequest request = DeleteItemRequest.builder()
            .tableName(this.table)
            .key(keymap)
            .build();
        Map<String, String> payload = new HashMap<>();
        payload.put("table", this.table);
        payload.put("uuid", uuid);
        try {
            client.deleteItem(request);
            payload.put("status", "deleted");
        } catch (ResourceNotFoundException e) {
            payload.put("error", "table not found");
            System.err.println(this.g.toJson(payload));
            System.exit(1);
        } catch (DynamoDbException e) {
            payload.put("error", e.getMessage());
            System.err.println(this.g.toJson(payload));
            System.exit(1);
        }
        return this.g.toJson(payload);
    }
}