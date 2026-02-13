package com.prem.jats.jats_backend.dto;

import java.time.LocalDate;

import com.prem.jats.jats_backend.entity.ApplicationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateJobRequest {

    private String company;
    private String role;
    private String location;
    private String jobLink;
    private String ctc;
    private LocalDate appliedDate;
    private ApplicationStatus status;
    private String resumeVersion;
}
