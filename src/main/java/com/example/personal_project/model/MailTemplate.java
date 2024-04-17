package com.example.personal_project.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MailTemplate extends ReadObject{
    private Long id;
    @JsonProperty("company_id")
    private Long companyId;
    private String content;
    private String picture;
    private String url;
}
