package com.example.personal_project.service;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;

import java.util.List;

public interface MailTemplateService {
    void insertNewTemplate(String account, MailTemplate mailTemplate);

    MailTemplate insertNewTemplateWithAccount(String account, MailTemplate mailTemplate);

    void deleteTemplate(Long id) throws Exception;

    void updateTemplate(MailTemplate mailTemplate);

    MailTemplate findMailTemplateById(Long templateId);

    MailTemplate getTemplateByCampaign(Campaign campaign);

    List<MailTemplate> getTemplatesByCompany(Long company_id);

    List<MailTemplate> getTemplateByAccount(String account);

    List<MailTemplate> getPageMailTemplateByCompany(String account, int paging);
}
