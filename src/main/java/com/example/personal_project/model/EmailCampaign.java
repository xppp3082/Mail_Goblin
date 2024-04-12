package com.example.personal_project.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EmailCampaign {
    private Campaign campaign;
    private List<Audience>audiences;
}
