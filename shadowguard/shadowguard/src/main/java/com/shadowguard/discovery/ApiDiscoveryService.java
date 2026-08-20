package com.shadowguard.discovery;

import com.shadowguard.entity.Api;
import com.shadowguard.repository.ApiRepository;
import com.shadowguard.security.RiskAnalyzer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApiDiscoveryService {

    private final RequestMappingHandlerMapping handlerMapping;
    private final ApiRepository apiRepository;
    private final RiskAnalyzer riskAnalyzer;

    public ApiDiscoveryService(
            RequestMappingHandlerMapping handlerMapping,
            ApiRepository apiRepository,
            RiskAnalyzer riskAnalyzer) {

        this.handlerMapping = handlerMapping;
        this.apiRepository = apiRepository;
        this.riskAnalyzer = riskAnalyzer;
    }

    public List<Api> discoverAndSaveApis() {

        List<Api> discoveredApis = new ArrayList<>();

        for (Map.Entry<RequestMappingInfo, ?> entry
                : handlerMapping.getHandlerMethods().entrySet()) {

            RequestMappingInfo mapping = entry.getKey();

            // HTTP methods
            var methods = mapping.getMethodsCondition().getMethods();

            // URL paths
            var pathPatterns = mapping.getPathPatternsCondition();

            if (pathPatterns == null) {
                continue;
            }

            for (var pathPattern : pathPatterns.getPatterns()) {

                String endpoint = pathPattern.toString();

                // Only API endpoints
                if (!endpoint.startsWith("/api/")) {
                    continue;
                }

                // Ignore ShadowGuard's own internal APIs
                if (isInternalShadowGuardApi(endpoint)) {
                    continue;
                }

                // If no HTTP method is explicitly defined
                if (methods.isEmpty()) {

                    saveApi(
                            "ANY",
                            endpoint,
                            discoveredApis
                    );

                } else {

                    for (var method : methods) {

                        saveApi(
                                method.name(),
                                endpoint,
                                discoveredApis
                        );
                    }
                }
            }
        }

        return discoveredApis;
    }

    /**
     * Checks whether an endpoint belongs to ShadowGuard itself.
     * These endpoints should not be treated as Shadow APIs.
     */
    private boolean isInternalShadowGuardApi(String endpoint) {

        return endpoint.startsWith("/api/discovery")
                || endpoint.startsWith("/api/apis")
                || endpoint.startsWith("/api/dashboard")
                || endpoint.startsWith("/api/projects")
                || endpoint.startsWith("/api/ai");
    }

    private void saveApi(
            String method,
            String endpoint,
            List<Api> discoveredApis) {

        // IMPORTANT:
        // Search only for an existing DISCOVERED API.
        // A DOCUMENTED API with the same method + endpoint
        // must remain a separate record.
        var existingApi =
                apiRepository.findByMethodAndEndpointAndSource(
                        method,
                        endpoint,
                        "DISCOVERED"
                );

        Api api;

        if (existingApi.isPresent()) {

            // Existing discovered API → re-analyze it
            api = existingApi.get();

        } else {

            // New discovered API
            api = new Api();

            api.setMethod(method);
            api.setEndpoint(endpoint);
            api.setSource("DISCOVERED");
            api.setAuthenticationRequired(false);
        }

        // Recalculate risk on every scan
        int riskScore =
                riskAnalyzer.calculateRiskScore(api);

        String riskLevel =
                riskAnalyzer.getRiskLevel(riskScore);

        List<String> reasons =
                riskAnalyzer.getRiskReasons(api);

        api.setRiskScore(riskScore);
        api.setRiskLevel(riskLevel);

        api.setRiskReasons(
                String.join("; ", reasons)
        );

        Api savedApi = apiRepository.save(api);

        discoveredApis.add(savedApi);
    }
}