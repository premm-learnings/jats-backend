package com.prem.jats.jats_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prem.jats.jats_backend.dto.ConversionStatsResponse;
import com.prem.jats.jats_backend.dto.OutcomeStatsResponse;
import com.prem.jats.jats_backend.dto.OverallStatsResponse;
import com.prem.jats.jats_backend.service.AnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // 🔹 Overall dashboard stats
    @GetMapping("/overview")
    public ResponseEntity<OverallStatsResponse> getOverview() {
        return ResponseEntity.ok(
                analyticsService.getOverallStats());
    }

    // 🔹 Conversion funnel stats
    @GetMapping("/conversion")
    public ResponseEntity<ConversionStatsResponse> getConversionStats() {
        return ResponseEntity.ok(
                analyticsService.getConversionStats());
    }

    @GetMapping("/outcomes")
public ResponseEntity<OutcomeStatsResponse> getOutcomeStats() {
    return ResponseEntity.ok(
            analyticsService.getOutcomeStats());
}

}
