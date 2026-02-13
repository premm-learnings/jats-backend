package com.prem.jats.jats_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prem.jats.jats_backend.entity.FollowUp;
import com.prem.jats.jats_backend.entity.JobApplication;

public interface FollowUpRepository
        extends JpaRepository<FollowUp, Long> {

    FollowUp findByJobApplication(JobApplication jobApplication);

    List<FollowUp> findByCompletedFalseAndFollowUpDateBefore(LocalDate date);
}
