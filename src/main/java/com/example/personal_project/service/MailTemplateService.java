package com.example.personal_project.service;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;

import java.util.List;

public interface MailTemplateService {
    void insertNewTemplate(MailTemplate mailTemplate);
    void deleteTemplate(Long id);
    void updateTemplate(MailTemplate mailTemplate);
    MailTemplate findMailTemplateById(Long templateId);
    MailTemplate getTemplateByCampaign(Campaign campaign);
    List <MailTemplate>getTemplatesByCompany(Long company_id);
}
