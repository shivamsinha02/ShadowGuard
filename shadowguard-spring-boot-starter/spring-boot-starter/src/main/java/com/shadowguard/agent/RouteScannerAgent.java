package com.shadowguard.agent;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteScannerAgent {

    private final RequestMappingHandlerMapping handlerMapping;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${shadowguard.dashboard.url:http://localhost:8080}")
    private String dashboardUrl;

    @Value("${spring.application.name:ClientApp}")
    private String applicationName;

    public RouteScannerAgent(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scanAndReport() {
        try {
            // 1. Automatically extract all endpoint strings from the hosting app
            List<String> routes = this.handlerMapping.getHandlerMethods().keySet().stream()
                    .flatMap(info -> info.getPatternValues().stream())
                    .collect(Collectors.toList());

            System.out.println("[ShadowGuard Agent] Intercepted routing table. Total endpoints found: " + routes.size());

            // 2. Prepare payload to match your Render backend DTO structure
            Map<String, Object> payload = Map.of(
                    "applicationName", applicationName,
                    "routes", routes
            );

            // 3. Fire-and-forget background thread so we do not block client startup sequence
            new Thread(() -> {
                try {
                    String targetEndpoint = dashboardUrl + "/api/agent/register";
                    restTemplate.postForObject(targetEndpoint, payload, String.class);
                    System.out.println("[ShadowGuard Agent] Telemetry synchronized successfully with central core.");
                } catch (Exception e) {
                    System.err.println("[ShadowGuard Agent] Core core unreachable: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            System.err.println("[ShadowGuard Agent] Scan failure: " + e.getMessage());
        }
    }
}
