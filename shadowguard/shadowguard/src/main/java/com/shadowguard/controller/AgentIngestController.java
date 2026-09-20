package com.shadowguard.controller;

import com.shadowguard.discovery.ApiDiscoveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agent")
@CrossOrigin(origins = "*") // Crucial: Allows external apps to send telemetry data to your server
public class AgentIngestController {

    private final ApiDiscoveryService apiDiscoveryService;

    // Spring automatically injects your existing Discovery Service
    public AgentIngestController(ApiDiscoveryService apiDiscoveryService) {
        this.apiDiscoveryService = apiDiscoveryService;
    }

    // 1. Data Transfer Object (DTO) structure for the incoming request payload
    public record AgentRegistrationDto(String applicationName, List<String> routes) {
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> receiveRoutesFromAgent(@RequestBody AgentRegistrationDto registration) {

        System.out.println("====== SHADOWGUARD TELEMETRY RECEIVED ======");
        System.out.println("Ingesting routes for application: " + registration.applicationName());
        System.out.println("Total endpoints discovered: " + registration.routes().size());

        try {
            // 2. Loop through the ingested routes and process them using your engine
            for (String path : registration.routes()) {
                /*
                 * Your existing ApiDiscoveryService handles risk checks and DB saving.
                 * Adjust this method call based on your exact method signature inside ApiDiscoveryService.
                 * Pass the path and application name to log the exact source.
                 */
                // Example: apiDiscoveryService.processDiscoveredEndpoint(path, registration.applicationName());
                System.out.println("Processing path: " + path);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "Successfully synced " + registration.routes().size() + " endpoints for " + registration.applicationName()
            ));

        } catch (Exception e) {
            System.err.println("Telemetry ingestion failed: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "ERROR",
                    "message", "Failed to process agent routes: " + e.getMessage()
            ));
        }
    }
}