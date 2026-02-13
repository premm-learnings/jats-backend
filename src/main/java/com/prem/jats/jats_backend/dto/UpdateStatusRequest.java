package com.prem.jats.jats_backend.dto;

import com.prem.jats.jats_backend.entity.ApplicationStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusRequest {
    private ApplicationStatus status;
}
