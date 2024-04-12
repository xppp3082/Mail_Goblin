package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Company {
    private Long id;
    private String title;
    private String description;
    private String industry;
    @JsonProperty("company_uuid")
    private String companyUUID;
    private Date anniversary;
}
