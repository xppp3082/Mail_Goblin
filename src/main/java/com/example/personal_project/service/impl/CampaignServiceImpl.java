package com.example.personal_project.service.impl;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.ExecuteStatus;
import com.example.personal_project.repository.CampaignRepo;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailTemplateService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService {
    private final MailServerService mailServerService;
    private final MailServiceImpl mailService;
    private final MailTemplateService mailTemplateService;
    private final AudienceServiceImpl audienceService;
    private final CampaignRepo campaignRepo;
    private final MailGunService mailGunService;

    @Value("${product.paging.size}")
    private int pagingSize;

    public CampaignServiceImpl(MailServerService mailServerService, MailServiceImpl mailService, MailTemplateService mailTemplateService, AudienceServiceImpl audienceService, CampaignRepo campaignRepo, MailGunService mailGunService) {
        this.mailServerService = mailServerService;
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
        this.audienceService = audienceService;
        this.campaignRepo = campaignRepo;
        this.mailGunService = mailGunService;
    }

    @Override
    public Campaign findCampaignById(Long id) {
        return campaignRepo.findCampaignById(id);
    }

    @Override
    public List<Campaign> getAllCampaignsByAccount(String account) {
        return campaignRepo.getAllCampaignsByAccount(account);
    }

    @Override
    public List<Campaign> getPageCampaignByAccount(String account, int paging) {
        int offset = paging * pagingSize;
        return campaignRepo.getPageCampaignByAccount(account, pagingSize + 1, offset);
    }

    @Override
    public List<Campaign> getPageCampaignByAccountWithTag(String account, int paging) {
        int offset = paging * pagingSize;
        return campaignRepo.getPageCampaignByAccountWithTag(account, pagingSize + 1, offset);
    }

    @Override
    public Integer getTotalCampaignCountByAccount(String account) {
        return campaignRepo.getTotalCampaignCountByAccount(account);
    }

    @Override
    public List<Campaign> getAllCompletedCampaigns() {
        return campaignRepo.getAllCompletedCampaigns();
    }

    @Override
    public void updateCampaignExecuteStatus(Campaign campaign) {
        campaignRepo.updateCampaignExecuteStatus(campaign);
    }

    @Override
    public void sendCampaign(Campaign campaign) throws MessagingException, UnsupportedEncodingException {
        List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
        MailTemplate mailTemplate = mailTemplateService.getTemplateByCampaign(campaign);
        EmailCampaign emailCampaign = new EmailCampaign(campaign, mailTemplate, audiences);
        try {
            List<Mail> mails = mailServerService.sendBatchMails(emailCampaign);
//            mailService.insertBatch(mails);
            mailService.updateBatchByMimeId(mails);
            log.info("batch insert mail record successfully");
        } catch (Exception e) {
            log.error("batch insert email record fail : " + e.getMessage());
        }
    }

    @Override
    public void sendCampaignById(Long id) throws MessagingException, UnsupportedEncodingException {
        Campaign targetCampaign = findCampaignById(id);
        sendCampaign(targetCampaign);
    }

    @Override
    public Campaign insertNewCampaign(Campaign campaign, String status) throws Exception {
        campaign.setStatus(status);
        campaign.setExecuteStatus(ExecuteStatus.PENDING.name());
        log.info(campaign.toString());
        return campaignRepo.insertNewCampaign(campaign);
    }

    @Override
    public void deleteCampaign(Long id) throws Exception {
        campaignRepo.deleteCampaign(id);
    }


}
