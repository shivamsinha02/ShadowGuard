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

                // Don't discover ShadowGuard's own scanner endpoint
                if (endpoint.startsWith("/api/discovery")) {
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

    private void saveApi(
            String method,
            String endpoint,
            List<Api> discoveredApis) {

        var existingApi =
                apiRepository.findByMethodAndEndpoint(
                        method,
                        endpoint
                );

        if (existingApi.isPresent()) {

            // Already present — don't create duplicate
            discoveredApis.add(existingApi.get());

            return;
        }

        Api api = new Api();

        api.setMethod(method);
        api.setEndpoint(endpoint);

        // Automatically discovered
        api.setSource("DISCOVERED");

        // Discovery currently cannot determine authentication.
        api.setAuthenticationRequired(false);

        // Calculate risk automatically
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