package com.prem.jats.jats_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prem.jats.jats_backend.entity.ApplicationStatus;
import com.prem.jats.jats_backend.entity.ApplicationStatusHistory;
import com.prem.jats.jats_backend.entity.JobApplication;
import com.prem.jats.jats_backend.entity.User;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {
             List<ApplicationStatusHistory>
        findByJobApplicationOrderByChangedAtAsc(JobApplication jobApplication);

        long countDistinctJobApplicationByNewStatusAndJobApplication_User(
        ApplicationStatus status,
        User user);

}
