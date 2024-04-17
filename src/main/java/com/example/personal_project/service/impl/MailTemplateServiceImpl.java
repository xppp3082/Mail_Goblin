package com.example.personal_project.service.impl;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;
import com.example.personal_project.repository.MailTemplateRepo;
import com.example.personal_project.service.MailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailTemplateServiceImpl implements MailTemplateService {
    public final MailTemplateRepo mailTemplateRepo;

    public MailTemplateServiceImpl(MailTemplateRepo mailTemplateRepo) {
        this.mailTemplateRepo = mailTemplateRepo;
    }

    @Override
    public void insertNewTemplate(MailTemplate mailTemplate) {
        mailTemplateRepo.insertMailTemplate(mailTemplate);
    }

    @Override
    public MailTemplate getTemplateByCampaign(Campaign campaign) {
        return mailTemplateRepo.getMailTemplateByCampaign(campaign);
    }
}
