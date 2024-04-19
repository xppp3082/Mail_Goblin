package com.example.personal_project.model.response;

import com.example.personal_project.model.dto.CompanyDto;
import lombok.Data;

@Data
public class LoginResponse {
    private String access_token;
    private Long access_expired;
    private CompanyDto companyDto;
}
