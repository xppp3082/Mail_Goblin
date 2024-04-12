package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
abstract class ReadObject {
    private String subject;
    @JsonProperty("send_date")
    private LocalDate sendDate;
//    private Date sendDate; //在campaign是targetDate 在email是sendDate
    private String status;
}
