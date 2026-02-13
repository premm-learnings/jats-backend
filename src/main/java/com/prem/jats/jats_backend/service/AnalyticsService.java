package com.prem.jats.jats_backend.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.prem.jats.jats_backend.dto.ConversionStatsResponse;
import com.prem.jats.jats_backend.dto.OutcomeStatsResponse;
import com.prem.jats.jats_backend.dto.OverallStatsResponse;
import com.prem.jats.jats_backend.entity.ApplicationStatus;
import com.prem.jats.jats_backend.entity.User;
import com.prem.jats.jats_backend.repository.ApplicationStatusHistoryRepository;
import com.prem.jats.jats_backend.repository.JobApplicationRepository;

@Service
public class AnalyticsService {

    private final JobApplicationRepository jobRepository;
    private final ApplicationStatusHistoryRepository historyRepository;

public AnalyticsService(
        JobApplicationRepository jobRepository,
        ApplicationStatusHistoryRepository historyRepository) {

    this.jobRepository = jobRepository;
    this.historyRepository = historyRepository;
}


  

    // 🔹 Overall counts
    public OverallStatsResponse getOverallStats() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        OverallStatsResponse response =
                new OverallStatsResponse();

        response.setTotalApplications(
                jobRepository.countByUserAndIsDeletedFalse(user));

        response.setSaved(
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.SAVED));

        response.setApplied(
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.APPLIED));

        response.setInterview(
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.INTERVIEW));

        response.setOffer(
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.OFFER));

        response.setRejected(
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.REJECTED));

        return response;
    }

    // 🔹 Conversion funnel
    public ConversionStatsResponse getConversionStats() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        long applied =
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.APPLIED);

        long interview =
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.INTERVIEW);

        long offer =
                jobRepository.countByUserAndStatusAndIsDeletedFalse(
                        user, ApplicationStatus.OFFER);


        ConversionStatsResponse response =
                new ConversionStatsResponse();

        response.setAppliedToInterviewRate(
                applied == 0 ? 0 :
                        (interview * 100.0) / applied);

        response.setInterviewToOfferRate(
                interview == 0 ? 0 :
                        (offer * 100.0) / interview);

        return response;
    }

    public OutcomeStatsResponse getOutcomeStats() {

    User user = (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();

        long totalApplications =
            jobRepository.countByUserAndIsDeletedFalse(user);

    long offerCount =
            historyRepository
                .countDistinctJobApplicationByNewStatusAndJobApplication_User(
                        ApplicationStatus.OFFER, user);

    long rejectedCount =
            historyRepository
                .countDistinctJobApplicationByNewStatusAndJobApplication_User(
                        ApplicationStatus.REJECTED, user);

    OutcomeStatsResponse response =
            new OutcomeStatsResponse();

    response.setOfferSuccessRate(
            totalApplications == 0 ? 0 :
                    (offerCount * 100.0) / totalApplications);

    response.setRejectionRate(
            totalApplications == 0 ? 0 :
                    (rejectedCount * 100.0) / totalApplications );

    return response;
}

}
