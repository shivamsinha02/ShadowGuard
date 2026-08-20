package com.shadowguard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
public class ShadowTestController {

    @GetMapping("/test")
    public String shadowTestApi() {
        return "Undocumented runtime API detected";
    }
}