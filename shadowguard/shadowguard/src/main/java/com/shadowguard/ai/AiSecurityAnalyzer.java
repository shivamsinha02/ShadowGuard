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

        try {

            GenerateContentResponse response =
                    geminiClient.models.generateContent(
                            "gemini-3.6-flash",
                            prompt,
                            null
                    );

            String result = response.text();

            if (result == null || result.isBlank()) {
                return getUnavailableMessage();
            }

            return result;

        } catch (Exception e) {

            System.out.println(
                    "Gemini AI unavailable: " + e.getMessage()
            );

            // AI failure must never break ShadowGuard scanning
            return getUnavailableMessage();
        }
    }

    private String getUnavailableMessage() {

        return """
                SUMMARY:
                AI analysis is temporarily unavailable. The security finding was still detected successfully by ShadowGuard.

                IMPACT:
                Please review the API risk score, risk level, authentication status, and detected risk reasons shown in the dashboard.

                RECOMMENDATION:
                Review the identified API manually and ensure appropriate authentication, authorization, and API documentation are in place.
                """;
    }
}