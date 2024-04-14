package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.SpringVersion;

@Slf4j
@Data
public class Tag {
    private Long id;
    @JsonProperty("company_id")
    private Long companyId;
    private String name;
}
