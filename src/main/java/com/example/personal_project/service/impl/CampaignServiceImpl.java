package com.example.personal_project.service.impl;

import com.example.personal_project.model.*;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.model.status.ExecuteStatus;
import com.example.personal_project.repository.CampaignRepo;
import com.example.personal_project.repository.MailTemplateRepo;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailService;
import com.example.personal_project.service.MailTemplateService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
    public CampaignServiceImpl(MailServerService mailServerService, MailServiceImpl mailService, MailTemplateService mailTemplateService, AudienceServiceImpl audienceService, CampaignRepo campaignRepo, MailGunService mailGunService) {
        this.mailServerService = mailServerService;
        this.mailService = mailService;
        this.mailTemplateService = mailTemplateService;
        this.audienceService = audienceService;
        this.campaignRepo = campaignRepo;
        this.mailGunService = mailGunService;
    }

    @Override
    public List<Campaign> getAllCampaignsByAccount(String account) {
        return campaignRepo.getAllCampaignsByAccount(account);
    }

    @Override
    public List<Campaign> getAllCompletedCampaigns() {
        return campaignRepo.getAllCompletedCampaigns();
    }

    @Override
    public void updateCampaignExecuteStatus(Campaign campaign) {
        campaignRepo.updateCampaignExecuteStatus(campaign);
    }

    public void sendCampaign(Campaign campaign) throws MessagingException, UnsupportedEncodingException {
        List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
        MailTemplate mailTemplate = mailTemplateService.getTemplateByCampaign(campaign);
        EmailCampaign emailCampaign = new EmailCampaign(campaign,mailTemplate,audiences);
        try{
            List<Mail> mails =  mailServerService.sendBatchMails2(emailCampaign);
            mailService.insertBatch(mails);
            log.info("batch insert mail record successfully");
        }catch (Exception e){
            log.error("batch insert email record fail : "+e.getMessage());
        }
    }

    @Override
    public Campaign insertNewCampaign(Campaign campaign,String status) {
        campaign.setStatus(status);
        campaign.setExecuteStatus(ExecuteStatus.PENDING.name());
        return campaignRepo.insertNewCampaign(campaign);
    }

    //根據Campaign的時稱安排進行自動排程寄送
    //目前每天早上八點檢查一次所有的Campaign
    //EC2時區和台灣時區不同
//    @Scheduled(cron = "0 0 8 * * ?")
    public void sendScheduledCampaign(){
        //取得DB中所有的Campaign
        log.info("Scheduled campaigns start!!");
        Date currentDate = new Date();
        LocalDate targetDate = LocalDate.now();
        List<Campaign> campaignsToSend = getAllCompletedCampaigns();
        for (Campaign campaign: campaignsToSend){
            log.info("campaign 預計要寄送的日期是: "+campaign.getSendDate());
            if(campaign.getSendDate().equals(targetDate)){
                log.info("今天有Campaign要發送喔!! " + campaign.toString());
                List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
                MailTemplate mailTemplate = mailTemplateService.getTemplateByCampaign(campaign);
                EmailCampaign emailCampaign = new EmailCampaign(campaign,mailTemplate,audiences);
                try{
                    List<Mail> mails =  mailServerService.sendBatchMails2(emailCampaign);
                    mailService.insertBatch(mails);
                    updateCampaignExecuteStatus(campaign);
                    log.info("batch insert mail record successfully");
                }catch (Exception e){
                    log.error("batch insert email record fail : "+e.getMessage());
                }
            }
        }
    }

}
