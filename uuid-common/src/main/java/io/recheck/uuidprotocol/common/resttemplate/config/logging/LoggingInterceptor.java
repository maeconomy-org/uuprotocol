package io.recheck.uuidprotocol.common.resttemplate.config.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final ObjectMapper ob = new ObjectMapper();

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);  // Log request details
        ClientHttpResponse response = execution.execute(request, body);  // Execute the request
        ClientHttpResponse bufferedResponse = new BufferingClientHttpResponseWrapper(response); // Wrap the response
        logResponse(bufferedResponse);  // Log response details
        return bufferedResponse; // Return the wrapped response
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        // Log details of the HTTP request
        logger.info("Request URI: {}", request.getURI());
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request Headers: {}", request.getHeaders());
        String requestBody = new String(body, StandardCharsets.UTF_8);
        if (StringUtils.hasText(requestBody)) {
            logger.info("Request Body: {}", bodyToStr(requestBody));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        // Read and log the response body
        String responseBody = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        logger.info("Response Status Code: {}", response.getStatusCode());
        logger.info("Response Headers: {}", response.getHeaders());
        if (StringUtils.hasText(responseBody)) {
            logger.info("Response Body: {}", bodyToStr(responseBody));
        }
    }

    @SneakyThrows
    private String bodyToStr(Object body) {
        String bodyStr;
        if (body instanceof String) {
            Object json = new JSONTokener((String) body).nextValue();
            if (json instanceof JSONObject) {
                bodyStr = new JSONObject((String) body).toString(2);
            }
            else if (json instanceof JSONArray) {
                bodyStr = new JSONArray((String) body).toString(2);
            }
            else {
                bodyStr = (String) body;
            }
        }
        else {
            bodyStr = ob.writeValueAsString(body);
        }

        return bodyStr;
    }
}
