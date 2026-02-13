package com.prem.jats.jats_backend.controller;
import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import com.prem.jats.jats_backend.dto.CreateJobRequest;
import com.prem.jats.jats_backend.dto.JobResponse;
import com.prem.jats.jats_backend.dto.StatusHistoryResponse;
import com.prem.jats.jats_backend.dto.UpdateStatusRequest;
import com.prem.jats.jats_backend.service.JobApplicationService;

@RestController
@RequestMapping("/api/jobs")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

@PostMapping
public ResponseEntity<JobResponse> createJob(
        @RequestBody CreateJobRequest request
) {
    JobResponse response = jobApplicationService.createJob(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)   // 201 Created
            .body(response);              // ✅ return JSON body
}



    @GetMapping
public ResponseEntity<List<JobResponse>> getMyJobs() {
    return ResponseEntity.ok(jobApplicationService.getMyJobs());
}

@PutMapping("/{jobId}/status")
public ResponseEntity<Void> updateJobStatus(
        @PathVariable Long jobId,
        @RequestBody UpdateStatusRequest request) {

    jobApplicationService.updateJobStatus(jobId, request);
    return ResponseEntity.ok().build();
}

@GetMapping("/{jobId}/history")
public ResponseEntity<List<StatusHistoryResponse>> getJobHistory(
        @PathVariable Long jobId) {

    return ResponseEntity.ok(
            jobApplicationService.getJobStatusHistory(jobId));
}


@GetMapping("/paged")
public ResponseEntity<Page<JobResponse>> getMyJobsPaged(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "appliedDate") String sortBy,
        @RequestParam(defaultValue = "desc") String direction) {

    return ResponseEntity.ok(
            jobApplicationService.getMyJobsPaged(
                    page, size, sortBy, direction));
}

@DeleteMapping("/{jobId}")
public ResponseEntity<Void> deleteJob(
        @PathVariable Long jobId) {

    jobApplicationService.softDeleteJob(jobId);
    return ResponseEntity.noContent().build();
}



}
