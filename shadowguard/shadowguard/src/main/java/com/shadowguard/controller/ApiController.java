package com.shadowguard.controller;

import com.shadowguard.entity.Api;
import com.shadowguard.service.ApiService;
import org.springframework.web.bind.annotation.*;
import com.shadowguard.analyzer.ShadowApiDetector;

import java.util.List;

@RestController
@RequestMapping("/api/apis")
@CrossOrigin(origins = "http://localhost:5173")
public class ApiController {

    private final ApiService apiService;
    private final ShadowApiDetector shadowApiDetector;

    public ApiController(
            ApiService apiService,
            ShadowApiDetector shadowApiDetector) {

        this.apiService = apiService;
        this.shadowApiDetector = shadowApiDetector;
    }

    // baaki code same rahega

    @PostMapping
    public Api saveApi(@RequestBody Api api) {
        return apiService.saveApi(api);
    }

    @GetMapping("/shadow")
    public List<Api> getShadowApis() {
        return shadowApiDetector.detectShadowApis();
    }

    @GetMapping
    public List<Api> getAllApis() {
        return apiService.getAllApis();
    }

    @GetMapping("/high-risk")
    public List<Api> getHighRiskApis() {
        return apiService.getHighRiskApis();
    }

    @PostMapping("/import")
    public String importOpenApi() throws Exception {
        apiService.importOpenApi("openapi.json");
        return "OpenAPI imported successfully";
    }

    @PostMapping("/import-logs")
    public String importApiLogs() {

        apiService.importApiLogs("api-logs.txt");

        return "API logs imported successfully";
    }
}
