package com.example.personal_project.service;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;

public interface MailTemplateService {
    void insertNewTemplate(MailTemplate mailTemplate);
    MailTemplate getTemplateByCampaign(Campaign campaign);
}
