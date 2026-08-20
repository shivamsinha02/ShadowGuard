package com.shadowguard.discovery;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowguard.entity.Api;

@RestController
@RequestMapping("/api/discovery")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "https://shadowguard-1.onrender.com"
})
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