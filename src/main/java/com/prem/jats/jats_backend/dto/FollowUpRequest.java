package com.prem.jats.jats_backend.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowUpRequest {
    private LocalDate followUpDate;
    private String note;
}
