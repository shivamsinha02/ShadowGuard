package com.shadowguard.parser;

import com.shadowguard.entity.Api;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
public class ApiLogParser {

    public List<Api> parse(String fileName) {

        List<Api> apis = new ArrayList<>();

        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "Log file not found: " + fileName
            );
        }

        Scanner scanner = new Scanner(inputStream);

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(" ", 2);

            if (parts.length != 2) {
                continue;
            }

            String method = parts[0];
            String endpoint = parts[1];

            Api api = new Api();

            api.setMethod(method.toUpperCase());
            api.setEndpoint(endpoint);
            api.setSource("DISCOVERED");
            api.setAuthenticationRequired(false);

            apis.add(api);
        }

        scanner.close();

        return apis;
    }

    public static void main(String[] args) {

        ApiLogParser parser = new ApiLogParser();

        List<Api> apis = parser.parse("api-logs.txt");

        for (Api api : apis) {
            System.out.println(
                    api.getMethod() + " " + api.getEndpoint()
            );
        }
    }
}
