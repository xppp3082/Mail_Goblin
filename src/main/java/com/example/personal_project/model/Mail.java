package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
public class Mail extends ReadObject{
    private Long id;
    @JsonProperty("company_id")
    private Long companyID;
    private String recipientName;
    @JsonProperty("recipient_mail")
    private String recipientMail;
    private Timestamp timestamp;
    @JsonProperty("checktimes")
    private Integer checkTimes;
}

