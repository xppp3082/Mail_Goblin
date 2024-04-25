package com.example.personal_project.service.impl;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;
import com.example.personal_project.repository.MailTemplateRepo;
import com.example.personal_project.service.MailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class MailTemplateServiceImpl implements MailTemplateService {
    public final MailTemplateRepo mailTemplateRepo;

    @Value("${product.paging.size}")
    private int pagingSize;

    public MailTemplateServiceImpl(MailTemplateRepo mailTemplateRepo) {
        this.mailTemplateRepo = mailTemplateRepo;
    }

    @Override
    public void insertNewTemplate(String account, MailTemplate mailTemplate) {
//        mailTemplateRepo.insertMailTemplate(mailTemplate);
        mailTemplateRepo.insertMailTemplateByAccount(account, mailTemplate);
    }

    @Override
    public MailTemplate insertNewTemplateWithAccount(String account, MailTemplate mailTemplate) {
        return mailTemplateRepo.insertMailTemplateWithAcount(account, mailTemplate);
    }

    @Override
    public void deleteTemplate(Long id) {
        mailTemplateRepo.deleteMailTemplate(id);
    }

    @Override
    public void updateTemplate(MailTemplate mailTemplate) {
        mailTemplateRepo.updateMailTemplate(mailTemplate);
    }

    @Override
    public MailTemplate findMailTemplateById(Long templateId) {
        return mailTemplateRepo.findMailTemplateById(templateId);
    }

    @Override
    public MailTemplate getTemplateByCampaign(Campaign campaign) {
        return mailTemplateRepo.getMailTemplateByCampaign(campaign);
    }

    @Override
    public List<MailTemplate> getTemplatesByCompany(Long company_id) {
        return mailTemplateRepo.getAllMailTemplateByCompany(company_id);
    }

    @Override
    public List<MailTemplate> getTemplateByAccount(String account) {
        return mailTemplateRepo.getAllMailTemplateByAccount(account);
    }

    @Override
    public List<MailTemplate> getPageMailTemplateByCompany(String account, int paging) {
        int offset = paging * pagingSize;
        return mailTemplateRepo.getPageMailTemplateByCompany(account, pagingSize + 1, offset);
    }
}
