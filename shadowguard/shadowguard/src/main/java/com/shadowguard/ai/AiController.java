package com.shadowguard.ai;

import com.shadowguard.entity.Api;
import com.shadowguard.repository.ApiRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    private final AiSecurityAnalyzer aiSecurityAnalyzer;
    private final ApiRepository apiRepository;

    public AiController(
            AiSecurityAnalyzer aiSecurityAnalyzer,
            ApiRepository apiRepository) {

        this.aiSecurityAnalyzer = aiSecurityAnalyzer;
        this.apiRepository = apiRepository;
    }

    @GetMapping("/analyze/{id}")
    public String analyzeApi(@PathVariable Long id) {

        Api api = apiRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("API not found")
                );

        return aiSecurityAnalyzer.analyzeApi(api);
    }
}