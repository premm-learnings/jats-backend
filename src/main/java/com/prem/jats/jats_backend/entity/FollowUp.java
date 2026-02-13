package com.prem.jats.jats_backend.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class FollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate followUpDate;
    private String note;

    private boolean completed = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private JobApplication jobApplication;
}
