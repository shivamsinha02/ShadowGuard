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

        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {

            try {

                GenerateContentResponse response =
                        geminiClient.models.generateContent(
                                "gemini-3.6-flash",
                                prompt,
                                null
                        );

                return response.text();

            } catch (Exception e) {

                System.out.println(
                        "Gemini AI request failed. Attempt "
                                + attempt + "/" + maxRetries
                );

                if (attempt == maxRetries) {
                    throw new RuntimeException(
                            "Gemini AI is temporarily unavailable. Please try again later."
                    );
                }

                try {
                    Thread.sleep(2000L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(
                            "AI analysis interrupted."
                    );
                }
            }
        }

        throw new RuntimeException("AI analysis failed.");
    }
}