package com.shadowguard.dashboard;

import com.shadowguard.analyzer.ShadowApiDetector;
import com.shadowguard.entity.Api;
import com.shadowguard.repository.ApiRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final ApiRepository apiRepository;
    private final ShadowApiDetector shadowApiDetector;

    public DashboardService(
            ApiRepository apiRepository,
            ShadowApiDetector shadowApiDetector) {

        this.apiRepository = apiRepository;
        this.shadowApiDetector = shadowApiDetector;
    }


    public Map<String, Object> getDashboardStats() {

        List<Api> apis = apiRepository.findAll();

        int totalApis = apis.size();

        int shadowApis = 0;
        int lowRisk = 0;
        int mediumRisk = 0;
        int highRisk = 0;
        int criticalRisk = 0;

        for (Api api : apis) {

            if ("DISCOVERED".equalsIgnoreCase(api.getSource())) {
                shadowApis++;
            }

            String riskLevel = api.getRiskLevel();

            if ("LOW".equalsIgnoreCase(riskLevel)) {
                lowRisk++;
            } else if ("MEDIUM".equalsIgnoreCase(riskLevel)) {
                mediumRisk++;
            } else if ("HIGH".equalsIgnoreCase(riskLevel)) {
                highRisk++;
            } else if ("CRITICAL".equalsIgnoreCase(riskLevel)) {
                criticalRisk++;
            }
        }

        Map<String, Object> stats = new HashMap<>();

        stats.put("totalApis", totalApis);
        stats.put("shadowApis", shadowApis);
        stats.put("lowRisk", lowRisk);
        stats.put("mediumRisk", mediumRisk);
        stats.put("highRisk", highRisk);
        stats.put("criticalRisk", criticalRisk);

        return stats;
    }
    public List<Api> getShadowApis() {
        return shadowApiDetector.detectShadowApis();
    }
}