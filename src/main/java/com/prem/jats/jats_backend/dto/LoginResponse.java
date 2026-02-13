package com.prem.jats.jats_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String email;
}
