package com.example.personal_project.model;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailCampaign {
    private Campaign campaign;
    private MailTemplate mailTemplate;
    private List<Audience>audiences;
}
