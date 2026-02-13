package com.prem.jats.jats_backend.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverdueFollowUpResponse {

    private Long jobId;
    private String company;
    private String role;
    private LocalDate followUpDate;
}
