package com.shadowguard.service;

import com.shadowguard.entity.Api;
import com.shadowguard.repository.ApiRepository;
import com.shadowguard.parser.OpenApiParser;
import org.springframework.stereotype.Service;
import com.shadowguard.parser.ApiLogParser;

import java.util.List;

@Service
public class ApiService {

    private final ApiRepository apiRepository;
    private final OpenApiParser openApiParser;
    private final ApiLogParser apiLogParser;

    public ApiService(
            ApiRepository apiRepository,
            OpenApiParser openApiParser,
            ApiLogParser apiLogParser) {

        this.apiRepository = apiRepository;
        this.openApiParser = openApiParser;
        this.apiLogParser = apiLogParser;
    }

    public Api saveApi(Api api) {

        boolean exists = apiRepository.existsByMethodAndEndpoint(
                api.getMethod(),
                api.getEndpoint()
        );

        if (exists) {
            return apiRepository
                    .findByMethodAndEndpoint(
                            api.getMethod(),
                            api.getEndpoint()
                    )
                    .orElse(null);
        }

        return apiRepository.save(api);
    }

    public List<Api> getAllApis() {
        return apiRepository.findAll();
    }

    public List<Api> getHighRiskApis() {

        return apiRepository.findByRiskLevelIn(
                List.of("HIGH", "CRITICAL")
        );
    }

    public void saveAllApis(List<Api> apis) {

        for (Api api : apis) {

            boolean exists =
                    apiRepository.existsByMethodAndEndpointAndSource(
                            api.getMethod(),
                            api.getEndpoint(),
                            api.getSource()
                    );

            if (!exists) {
                apiRepository.save(api);
            }
        }
    }

    public void importOpenApi(String fileName) throws Exception {

        List<Api> apis = openApiParser.parse(fileName);

        saveAllApis(apis);
    }

    public void importApiLogs(String fileName) {

        List<Api> apis = apiLogParser.parse(fileName);

        saveAllApis(apis);
    }
}
