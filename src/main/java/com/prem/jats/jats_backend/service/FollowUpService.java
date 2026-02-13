package com.prem.jats.jats_backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.prem.jats.jats_backend.dto.FollowUpRequest;
import com.prem.jats.jats_backend.dto.FollowUpResponse;
import com.prem.jats.jats_backend.dto.OverdueFollowUpResponse;
import com.prem.jats.jats_backend.entity.FollowUp;
import com.prem.jats.jats_backend.entity.JobApplication;
import com.prem.jats.jats_backend.entity.User;
import com.prem.jats.jats_backend.repository.FollowUpRepository;
import com.prem.jats.jats_backend.repository.JobApplicationRepository;

@Service
public class FollowUpService {

    private final FollowUpRepository followUpRepository;
    private final JobApplicationRepository jobRepository;

    public FollowUpService(FollowUpRepository followUpRepository, JobApplicationRepository jobRepository) {
        this.followUpRepository = followUpRepository;
        this.jobRepository = jobRepository;
    }

    // 🔹 Create or update follow-up
    public FollowUpResponse setFollowUp(Long jobId, FollowUpRequest request) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobApplication job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        FollowUp followUp = followUpRepository.findByJobApplication(job);
        if (followUp == null) {
            followUp = new FollowUp();
            followUp.setJobApplication(job);
        }

        followUp.setFollowUpDate(request.getFollowUpDate());
        followUp.setNote(request.getNote());
        followUp.setCompleted(false);

        FollowUp saved = followUpRepository.save(followUp);
        return mapToResponse(saved);
    }

    // 🔹 Get follow-up for a job
    public FollowUpResponse getFollowUp(Long jobId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobApplication job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        FollowUp followUp = followUpRepository.findByJobApplication(job);
        if (followUp == null) {
            return null;
        }

        return mapToResponse(followUp);
    }

    // 🔹 Mark follow-up as completed
    public void completeFollowUp(Long jobId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobApplication job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        FollowUp followUp = followUpRepository.findByJobApplication(job);
        if (followUp == null) {
            throw new RuntimeException("No follow-up exists for this job");
        }

        followUp.setCompleted(true);
        followUpRepository.save(followUp);
    }

    // 🔹 Map entity to response DTO
    private FollowUpResponse mapToResponse(FollowUp followUp) {
        FollowUpResponse response = new FollowUpResponse();
        response.setId(followUp.getId());
        response.setFollowUpDate(followUp.getFollowUpDate());
        response.setNote(followUp.getNote());
        response.setCompleted(followUp.isCompleted());
        return response;
    }

    // 🔹 Get overdue follow-ups
    public List<OverdueFollowUpResponse> getOverdueFollowUps() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDate today = LocalDate.now();

        return followUpRepository.findByCompletedFalseAndFollowUpDateBefore(today)
                .stream()
                .filter(fu -> fu.getJobApplication().getUser().getId().equals(user.getId()))
                .map(fu -> {
                    OverdueFollowUpResponse response = new OverdueFollowUpResponse();
                    response.setJobId(fu.getJobApplication().getId());
                    response.setCompany(fu.getJobApplication().getCompany());
                    response.setRole(fu.getJobApplication().getRole());
                    response.setFollowUpDate(fu.getFollowUpDate());
                    return response;
                })
                .toList();
    }
}