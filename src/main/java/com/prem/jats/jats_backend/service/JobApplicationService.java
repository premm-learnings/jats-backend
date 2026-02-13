package com.prem.jats.jats_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.prem.jats.jats_backend.dto.CreateJobRequest;
import com.prem.jats.jats_backend.dto.JobResponse;
import com.prem.jats.jats_backend.dto.StatusHistoryResponse;
import com.prem.jats.jats_backend.dto.UpdateStatusRequest;
import com.prem.jats.jats_backend.entity.ApplicationStatus;
import com.prem.jats.jats_backend.entity.ApplicationStatusHistory;
import com.prem.jats.jats_backend.entity.JobApplication;
import com.prem.jats.jats_backend.entity.User;
import com.prem.jats.jats_backend.exception.AccessDeniedException;
import com.prem.jats.jats_backend.exception.InvalidStatusTransitionException;
import com.prem.jats.jats_backend.repository.ApplicationStatusHistoryRepository;
import com.prem.jats.jats_backend.repository.JobApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final ApplicationStatusHistoryRepository historyRepository;


   public JobApplicationService(
        JobApplicationRepository jobApplicationRepository,
        ApplicationStatusHistoryRepository historyRepository) {

    this.jobApplicationRepository = jobApplicationRepository;
    this.historyRepository = historyRepository;
}

    public JobResponse createJob(CreateJobRequest request) {

        // 1️⃣ Get logged-in user from SecurityContext
        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // 2️⃣ Create JobApplication entity
        JobApplication job = new JobApplication();
        job.setCompany(request.getCompany());
        job.setRole(request.getRole());
        job.setLocation(request.getLocation());
        job.setJobLink(request.getJobLink());
        job.setCtc(request.getCtc());
        job.setAppliedDate(request.getAppliedDate());
        job.setStatus(request.getStatus());
        job.setResumeVersion(request.getResumeVersion());

        // 3️⃣ Attach user
        job.setUser(user);

        // 4️⃣ Save job
        JobApplication savedJob = jobApplicationRepository.save(job);

        // 5️⃣ Map to response DTO
        JobResponse response = new JobResponse();
        response.setId(savedJob.getId());
        response.setCompany(savedJob.getCompany());
        response.setRole(savedJob.getRole());
        response.setLocation(savedJob.getLocation());
        response.setJobLink(savedJob.getJobLink());
        response.setCtc(savedJob.getCtc());
        response.setAppliedDate(savedJob.getAppliedDate());
        response.setStatus(savedJob.getStatus());
        response.setResumeVersion(savedJob.getResumeVersion());

        return response;
    }

    public List<JobResponse> getMyJobs() {

    // 1️⃣ Get logged-in user
    User user = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    // 2️⃣ Fetch jobs for user
    List<JobApplication> jobs =
            jobApplicationRepository.findByUserAndIsDeletedFalse(user);

    // 3️⃣ Map entities to DTOs
    return jobs.stream().map(job -> {
        JobResponse response = new JobResponse();
        response.setId(job.getId());
        response.setCompany(job.getCompany());
        response.setRole(job.getRole());
        response.setLocation(job.getLocation());
        response.setJobLink(job.getJobLink());
        response.setCtc(job.getCtc());
        response.setAppliedDate(job.getAppliedDate());
        response.setStatus(job.getStatus());
        response.setResumeVersion(job.getResumeVersion());
        return response;
    }).toList();
}

private boolean isValidTransition(
        ApplicationStatus current,
        ApplicationStatus next) {

    return switch (current) {
        case SAVED -> next == ApplicationStatus.APPLIED;
        case APPLIED ->
                next == ApplicationStatus.INTERVIEW ||
                next == ApplicationStatus.REJECTED;
        case INTERVIEW ->
                next == ApplicationStatus.OFFER ||
                next == ApplicationStatus.REJECTED;
        case OFFER, REJECTED -> false;
    };
}

public void updateJobStatus(Long jobId, UpdateStatusRequest request) {

    // 1️⃣ Get logged-in user
    User user = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    // 2️⃣ Fetch job
    JobApplication job = jobApplicationRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

    // 3️⃣ Ownership check
    if (!job.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    // 4️⃣ Validate transition
    ApplicationStatus currentStatus = job.getStatus();
    ApplicationStatus nextStatus = request.getStatus();

    if (!isValidTransition(currentStatus, nextStatus)) {
        throw new InvalidStatusTransitionException(
                "Invalid transition from " +
                currentStatus + " to " + nextStatus);
    }

    // 5️⃣ Update job status
    job.setStatus(nextStatus);
    jobApplicationRepository.save(job);

    // 6️⃣ Save status history
    ApplicationStatusHistory history =
            new ApplicationStatusHistory();

    history.setOldStatus(currentStatus);
    history.setNewStatus(nextStatus);
    history.setChangedAt(LocalDateTime.now());
    history.setJobApplication(job);

    historyRepository.save(history);
}

public List<StatusHistoryResponse> getJobStatusHistory(Long jobId) {

    // 1️⃣ Get logged-in user
    User user = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    // 2️⃣ Fetch job
    JobApplication job = jobApplicationRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

    // 3️⃣ Ownership check
    if (!job.getUser().getId().equals(user.getId())) {
        throw new AccessDeniedException("Access denied");
    }

    // 4️⃣ Fetch history
    List<ApplicationStatusHistory> historyList =
            historyRepository.findByJobApplicationOrderByChangedAtAsc(job);

    // 5️⃣ Map to DTO
    return historyList.stream().map(history -> {
        StatusHistoryResponse response =
                new StatusHistoryResponse();

        response.setOldStatus(history.getOldStatus());
        response.setNewStatus(history.getNewStatus());
        response.setChangedAt(history.getChangedAt());

        return response;
    }).toList();
}




public Page<JobResponse> getMyJobsPaged(
        int page,
        int size,
        String sortBy,
        String direction) {

    User user = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    PageRequest pageable =
            PageRequest.of(page, size, sort);

    return jobApplicationRepository
            .findByUserAndIsDeletedFalse(user, pageable)
            .map(job -> {
                JobResponse response = new JobResponse();
                response.setId(job.getId());
                response.setCompany(job.getCompany());
                response.setRole(job.getRole());
                response.setLocation(job.getLocation());
                response.setJobLink(job.getJobLink());
                response.setCtc(job.getCtc());
                response.setAppliedDate(job.getAppliedDate());
                response.setStatus(job.getStatus());
                response.setResumeVersion(job.getResumeVersion());
                return response;
            });
}

public void softDeleteJob(Long jobId) {

    User user = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

    JobApplication job = jobApplicationRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

    if (!job.getUser().getId().equals(user.getId())) {
        throw new RuntimeException("Access denied");
    }

    if (job.isDeleted()) {
        return; // idempotent delete
    }

    job.setDeleted(true);
    jobApplicationRepository.save(job);
}



}
