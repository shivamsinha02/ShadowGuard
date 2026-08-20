package com.shadowguard.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowguard.analyzer.ShadowApiDetector;
import com.shadowguard.entity.Api;
import com.shadowguard.service.ApiService;

import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/apis")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://shadowguard-1.onrender.com"
})
public class ApiController {

    private final ApiService apiService;
    private final ShadowApiDetector shadowApiDetector;

    public ApiController(
            ApiService apiService,
            ShadowApiDetector shadowApiDetector) {

        this.apiService = apiService;
        this.shadowApiDetector = shadowApiDetector;
    }


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

    @DeleteMapping("/reset")
    public String resetApis() {
        apiService.deleteAllApis();
        return "API data reset successfully";
    }

}
