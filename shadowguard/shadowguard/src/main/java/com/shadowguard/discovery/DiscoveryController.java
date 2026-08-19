package com.shadowguard.discovery;

import com.shadowguard.entity.Api;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discovery")
@CrossOrigin(origins = "http://localhost:5173")
public class DiscoveryController {

    private final ApiDiscoveryService discoveryService;

    public DiscoveryController(ApiDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @GetMapping("/scan")
    public List<Api> scanApis() {
        return discoveryService.discoverAndSaveApis();
    }
}