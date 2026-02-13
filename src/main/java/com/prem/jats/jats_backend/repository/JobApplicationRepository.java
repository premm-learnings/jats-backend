package com.prem.jats.jats_backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.prem.jats.jats_backend.entity.ApplicationStatus;
import com.prem.jats.jats_backend.entity.JobApplication;
import com.prem.jats.jats_backend.entity.User;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

     List<JobApplication> findByUserAndIsDeletedFalse(User user);

     long countByUserAndIsDeletedFalse(User user);

long countByUserAndStatusAndIsDeletedFalse(
        User user,
        ApplicationStatus status);

        Page<JobApplication> findByUserAndIsDeletedFalse(
        User user,
        Pageable pageable);


}
