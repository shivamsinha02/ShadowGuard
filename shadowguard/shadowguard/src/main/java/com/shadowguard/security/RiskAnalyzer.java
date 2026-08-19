package com.shadowguard.security;

import com.shadowguard.entity.Api;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RiskAnalyzer {

    public int calculateRiskScore(Api api) {

        int score = 0;

        // Shadow / undocumented API
        if ("DISCOVERED".equalsIgnoreCase(api.getSource())) {
            score += 30;
        }

        // Authentication missing
        if (!api.isAuthenticationRequired()) {
            score += 30;
        }

        String endpoint = api.getEndpoint().toLowerCase();

        // Sensitive endpoint keywords
        if (endpoint.contains("/debug")) {
            score += 20;
        }

        if (endpoint.contains("/internal")) {
            score += 20;
        }

        if (endpoint.contains("/admin")) {
            score += 20;
        }

        if (endpoint.contains("/config")) {
            score += 20;
        }

        if (endpoint.contains("/payment")) {
            score += 15;
        }


        // Maximum score = 100
        return Math.min(score, 100);
    }

    public String getRiskLevel(int score) {

        if (score >= 80) {
            return "CRITICAL";
        }

        if (score >= 60) {
            return "HIGH";
        }

        if (score >= 30) {
            return "MEDIUM";
        }

        return "LOW";
    }

    public static void main(String[] args) {

        RiskAnalyzer analyzer = new RiskAnalyzer();

        Api api1 = new Api();
        api1.setMethod("GET");
        api1.setEndpoint("/api/debug/users");
        api1.setSource("DISCOVERED");
        api1.setAuthenticationRequired(false);

        int score1 = analyzer.calculateRiskScore(api1);

        System.out.println(
                api1.getEndpoint() + " → "
                        + score1 + " → "
                        + analyzer.getRiskLevel(score1)
        );


        Api api2 = new Api();
        api2.setMethod("GET");
        api2.setEndpoint("/api/internal/config");
        api2.setSource("DISCOVERED");
        api2.setAuthenticationRequired(false);

        int score2 = analyzer.calculateRiskScore(api2);

        System.out.println(
                api2.getEndpoint() + " → "
                        + score2 + " → "
                        + analyzer.getRiskLevel(score2)
        );


        Api api3 = new Api();
        api3.setMethod("GET");
        api3.setEndpoint("/api/test/payment");
        api3.setSource("DISCOVERED");
        api3.setAuthenticationRequired(false);

        int score3 = analyzer.calculateRiskScore(api3);

        System.out.println(
                api3.getEndpoint() + " → "
                        + score3 + " → "
                        + analyzer.getRiskLevel(score3)
        );
    }

    public List<String> getRiskReasons(Api api) {

        List<String> reasons = new ArrayList<>();

        if ("DISCOVERED".equalsIgnoreCase(api.getSource())) {
            reasons.add("Undocumented API");
        }

        if (!api.isAuthenticationRequired()) {
            reasons.add("Authentication not required");
        }

        String endpoint = api.getEndpoint().toLowerCase();

        if (endpoint.contains("/debug")) {
            reasons.add("Debug endpoint");
        }

        if (endpoint.contains("/internal")) {
            reasons.add("Internal endpoint");
        }

        if (endpoint.contains("/admin")) {
            reasons.add("Admin endpoint");
        }

        if (endpoint.contains("/config")) {
            reasons.add("Configuration endpoint");
        }

        if (endpoint.contains("/payment")) {
            reasons.add("Payment-related endpoint");
        }
        if (endpoint.contains("/user")) {
            reasons.add("User-related endpoint");
        }

        if (endpoint.contains("/auth")) {
            reasons.add("Authentication-related endpoint");
        }

        return reasons;
    }
}