package com.prem.jats.jats_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.prem.jats.jats_backend.dto.FollowUpRequest;
import com.prem.jats.jats_backend.dto.FollowUpResponse;
import com.prem.jats.jats_backend.dto.OverdueFollowUpResponse;
import com.prem.jats.jats_backend.service.FollowUpService;

@RestController
@RequestMapping("/api/jobs")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    // 🔹 Set or update follow-up
    @PostMapping("/{jobId}/followup")
    public ResponseEntity<FollowUpResponse> setFollowUp(
            @PathVariable Long jobId,
            @RequestBody FollowUpRequest request) {

        return ResponseEntity.ok(
                followUpService.setFollowUp(jobId, request));
    }

    // 🔹 Mark follow-up as completed
    @PutMapping("/{jobId}/followup/complete")
    public ResponseEntity<Void> completeFollowUp(
            @PathVariable Long jobId) {

        followUpService.completeFollowUp(jobId);
        return ResponseEntity.ok().build();
    }

    // 🔹 Get follow-up for job
    @GetMapping("/{jobId}/followup")
    public ResponseEntity<FollowUpResponse> getFollowUp(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                followUpService.getFollowUp(jobId));
    }

    @GetMapping("/followups/overdue")
public ResponseEntity<List<OverdueFollowUpResponse>>
        getOverdueFollowUps() {

    return ResponseEntity.ok(
            followUpService.getOverdueFollowUps());
}

}
