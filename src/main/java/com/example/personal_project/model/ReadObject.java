package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class ReadObject {
    private String subject;
    @JsonProperty("send_date")
    private LocalDate sendDate;
    //    private Date sendDate; //在campaign是targetDate 在email是sendDate
    private String status;
}
