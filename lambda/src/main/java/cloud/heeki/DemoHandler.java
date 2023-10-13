package cloud.heeki;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

// import cloud.heeki.proxy.AwsLatticeExceptionHandler;
// import cloud.heeki.proxy.AwsLatticeSecurityContextWriter;
// import cloud.heeki.proxy.model.LatticeRequest;
// import cloud.heeki.proxy.model.LatticeResponse;
// import cloud.heeki.proxy.servlet.AwsLatticeRequestReader;
// import cloud.heeki.proxy.servlet.AwsLatticeResponseWriter;

// import com.amazonaws.serverless.proxy.AwsProxyExceptionHandler;
// import com.amazonaws.serverless.proxy.AwsProxySecurityContextWriter;
// import com.amazonaws.serverless.proxy.InitializationWrapper;
// import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletRequestReader;
// import com.amazonaws.serverless.proxy.internal.servlet.AwsProxyHttpServletResponseWriter;
// import com.amazonaws.serverless.proxy.spring.SpringLambdaContainerHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class DemoHandler implements RequestStreamHandler {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    // private static SpringLambdaContainerHandler<LatticeRequest, LatticeResponse> handler;
    private static Gson g = new Gson();

    static {
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(DemoApplication.class);
            handler.onStartup(servletContext -> {
                ArrayList<String> scope = new ArrayList<String>();
                scope.add("_HANDLER");
                scope.add("AWS_EXECUTION_ENV");
                scope.add("AWS_LAMBDA_FUNCTION_HANDLER");
                scope.add("AWS_LAMBDA_FUNCTION_MEMORY_SIZE");
                scope.add("AWS_LAMBDA_FUNCTION_NAME");
                scope.add("AWS_LAMBDA_FUNCTION_TIMEOUT");
                scope.add("AWS_LAMBDA_FUNCTION_VERSION");
                scope.add("AWS_LAMBDA_RUNTIME_API");
                scope.add("AWS_REGION");
                scope.add("AWS_SAM_LOCAL");
                scope.add("LD_LIBRARY_PATH");
                scope.add("PATH");
                HashMap<String, String> debug = new HashMap();
                for (String var : scope) {
                    debug.put(var, System.getenv(var));
                }
                System.out.println(g.toJson(debug));
            });

            // reference: https://github.com/awslabs/aws-serverless-java-container/wiki/Custom-event-types
            // custom event handling for the new lattice event payload
            // AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
            // applicationContext.register(DemoApplication.class);
            // handler = new SpringLambdaContainerHandler<LatticeRequest, LatticeResponse>(
            //     LatticeRequest.class,
            //     LatticeResponse.class,
            //     new AwsLatticeRequestReader(),
            //     new AwsLatticeResponseWriter(),
            //     new AwsLatticeSecurityContextWriter(),
            //     new AwsLatticeExceptionHandler(),
            //     applicationContext,
            //     new InitializationWrapper()
            // );
        } catch (ContainerInitializationException e) {
        // } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize Spring Boot application", e);
        }
    }

    public DemoHandler() {
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
