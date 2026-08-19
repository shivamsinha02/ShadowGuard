package com.shadowguard.analyzer;

import com.shadowguard.entity.Api;
import com.shadowguard.repository.ApiRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.shadowguard.security.RiskAnalyzer;

@Service
public class ShadowApiDetector {

    private final RiskAnalyzer riskAnalyzer;

    private final ApiRepository apiRepository;

    public ShadowApiDetector(
            ApiRepository apiRepository,
            RiskAnalyzer riskAnalyzer) {

        this.apiRepository = apiRepository;
        this.riskAnalyzer = riskAnalyzer;
    }

    public List<Api> detectShadowApis() {

        List<Api> allApis = apiRepository.findAll();

        Set<String> documentedApis = new HashSet<>();

        for (Api api : allApis) {
            if (api.getSource().equals("DOCUMENTED")) {
                documentedApis.add(
                        api.getMethod() + ":" + api.getEndpoint()
                );
            }
        }

        List<Api> shadowApis = allApis.stream()
                .filter(api -> api.getSource().equals("DISCOVERED"))
                .filter(api ->
                        !documentedApis.contains(
                                api.getMethod() + ":" + api.getEndpoint()
                        )
                )
                .collect(Collectors.toMap(
                        api -> api.getMethod() + ":" + api.getEndpoint(),
                        api -> api,
                        (existing, duplicate) -> existing
                ))
                .values()
                .stream()
                .toList();

        shadowApis.forEach(api -> {

            int score = riskAnalyzer.calculateRiskScore(api);

            api.setRiskScore(score);
            api.setRiskLevel(riskAnalyzer.getRiskLevel(score));

            List<String> reasons = riskAnalyzer.getRiskReasons(api);

            api.setRiskReasons(String.join("; ", reasons));
        });

        apiRepository.saveAll(shadowApis);

        return shadowApis;
    }
}
