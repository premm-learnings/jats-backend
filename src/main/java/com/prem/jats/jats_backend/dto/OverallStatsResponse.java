package com.prem.jats.jats_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OverallStatsResponse {

    private long totalApplications;
    private long saved;
    private long applied;
    private long interview;
    private long offer;
    private long rejected;
}
