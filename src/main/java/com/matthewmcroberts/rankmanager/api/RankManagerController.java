package com.matthewmcroberts.rankmanager.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RankManagerController {

    // Public endpoint: no auth required
    @GetMapping("/api/public/health")
    public String healthCheck() {
        return "API is running!";
    }

    // Secure endpoint: requires token
    @GetMapping("/api/secure/rank")
    public String getRank() {
        return "Your rank is Admin";
    }
}