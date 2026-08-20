package com.shadowguard.ai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.shadowguard.entity.Api;
import org.springframework.stereotype.Service;

@Service
public class AiSecurityAnalyzer {

    private final Client geminiClient;

    public AiSecurityAnalyzer(Client geminiClient) {
        this.geminiClient = geminiClient;
    }

    public String analyzeApi(Api api) {

        String prompt = """
                You are a cybersecurity expert analyzing an API security finding.

                Analyze this API:

                HTTP Method: %s
                Endpoint: %s
                Source: %s
                Authentication Required: %s
                Risk Score: %d
                Risk Level: %s
                Risk Reasons: %s

                Provide a concise security analysis using exactly these sections:

                SUMMARY:
                Explain why this API is risky.

                IMPACT:
                Explain the possible security impact.

                RECOMMENDATION:
                Explain what the developer should do to secure this API.

                Keep the explanation practical and easy to understand.
                Do not invent facts that are not provided.
                """.formatted(
                api.getMethod(),
                api.getEndpoint(),
                api.getSource(),
                api.isAuthenticationRequired(),
                api.getRiskScore(),
                api.getRiskLevel(),
                api.getRiskReasons()
        );

        String[] models = {
                "gemini-3.6-flash",
                "gemini-3.5-flash"
        };

        Exception lastException = null;

        for (String model : models) {

            for (int attempt = 1; attempt <= 2; attempt++) {

                try {

                    System.out.println(
                            "Gemini request: model=" + model
                                    + ", attempt=" + attempt
                    );

                    GenerateContentResponse response =
                            geminiClient.models.generateContent(
                                    model,
                                    prompt,
                                    null
                            );

                    String result = response.text();

                    if (result != null && !result.isBlank()) {
                        return result;
                    }

                    throw new RuntimeException(
                            "Gemini returned an empty response"
                    );

                } catch (Exception e) {

                    lastException = e;

                    System.err.println(
                            "Gemini request failed: model="
                                    + model
                                    + ", attempt="
                                    + attempt
                    );

                    System.err.println(
                            "Error: " + e.getMessage()
                    );

                    e.printStackTrace();

                    if (attempt < 2) {
                        try {
                            Thread.sleep(1500L);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(
                                    "AI analysis interrupted."
                            );
                        }
                    }
                }
            }
        }

        throw new RuntimeException(
                "Gemini AI request failed after all attempts: "
                        + (lastException != null
                        ? lastException.getMessage()
                        : "Unknown error")
        );
    }
}