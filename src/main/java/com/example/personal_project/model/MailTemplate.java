package com.example.personal_project.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MailTemplate extends ReadObject{
    private Long id;
    @JsonProperty("company_id")
    private Long companyId;
    private String content;
    private String picture;
    private String url;
}
