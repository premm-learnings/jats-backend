package com.prem.jats.jats_backend.dto;

import java.time.LocalDateTime;

import com.prem.jats.jats_backend.entity.ApplicationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusHistoryResponse {

    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;
    private LocalDateTime changedAt;
}
