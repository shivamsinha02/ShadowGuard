package com.shadowguard.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowguard.entity.Api;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OpenApiParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Api> parse(String fileName) throws Exception {

        List<Api> apis = new ArrayList<>();

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "OpenAPI file not found: " + fileName
            );
        }

        JsonNode root = objectMapper.readTree(inputStream);

        JsonNode paths = root.get("paths");

        if (paths == null) {
            return apis;
        }

        paths.fields().forEachRemaining(pathEntry -> {

            String endpoint = pathEntry.getKey();
            JsonNode methods = pathEntry.getValue();

            methods.fieldNames().forEachRemaining(method -> {

                if (isHttpMethod(method)) {

                    Api api = new Api();

                    api.setMethod(method.toUpperCase());
                    api.setEndpoint(endpoint);
                    api.setSource("DOCUMENTED");
                    api.setAuthenticationRequired(false);

                    apis.add(api);
                }
            });
        });

        return apis;
    }

    private boolean isHttpMethod(String method) {

        return method.equalsIgnoreCase("get")
                || method.equalsIgnoreCase("post")
                || method.equalsIgnoreCase("put")
                || method.equalsIgnoreCase("delete")
                || method.equalsIgnoreCase("patch")
                || method.equalsIgnoreCase("options")
                || method.equalsIgnoreCase("head");
    }

    public static void main(String[] args) throws Exception {

        OpenApiParser parser = new OpenApiParser();

        List<Api> apis = parser.parse("openapi.json");

        for (Api api : apis) {
            System.out.println(
                    api.getMethod() + " " + api.getEndpoint()
            );
        }
    }

}