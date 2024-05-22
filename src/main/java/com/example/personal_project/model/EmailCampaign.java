package com.example.personal_project.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailCampaign {
    private Campaign campaign;
    private MailTemplate mailTemplate;
    private List<Audience> audiences;
}
