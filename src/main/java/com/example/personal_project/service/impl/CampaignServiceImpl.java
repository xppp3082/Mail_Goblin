package com.example.personal_project.service.impl;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.model.Mail;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.repository.CampaignRepo;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailService;
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
    private final AudienceServiceImpl audienceService;
    private final CampaignRepo campaignRepo;
    public CampaignServiceImpl(MailServerService mailServerService, MailServiceImpl mailService, AudienceServiceImpl audienceService, CampaignRepo campaignRepo) {
        this.mailServerService = mailServerService;
        this.mailService = mailService;
        this.audienceService = audienceService;
        this.campaignRepo = campaignRepo;
    }

    @Override
    public List<Campaign> getAllCampaigns() {
        return campaignRepo.getAllCampaigns();
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
        EmailCampaign emailCampaign = new EmailCampaign(campaign,audiences);
        List<Mail>mails = new ArrayList<>();
        for(Audience audience: emailCampaign.getAudiences()){
            Mail mail = new Mail();
            mail.setCompanyID(campaign.getId());
            mail.setRecipientMail(audience.getEmail());
            mail.setSubject(emailCampaign.getCampaign().getSubject());
            mail.setStatus(DeliveryStatus.PENDING.name());
            mail.setSendDate(LocalDate.now());
            mail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
            mail.setCheckTimes(0);
            mails.add(mail);
        }
        try{
            mailService.insertBatch(mails);
            mailServerService.sendBatchMails(emailCampaign);
            log.info("batch insert mail record successfully");
        }catch (Exception e){
            log.error("batch insert email record fail : "+e.getMessage());
        }
    }

    //根據Campaign的時稱安排進行自動排程寄送
    //目前每天早上八點檢查一次所有的Campaign
    //EC2時區和台灣時區不同
    @Scheduled(cron = "0 15 13 * * ?")
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
                EmailCampaign emailCampaign = new EmailCampaign(campaign,audiences);
                List<Mail>mails = new ArrayList<>();
                for(Audience audience: emailCampaign.getAudiences()){
                    Mail mail = new Mail();
                    mail.setCompanyID(campaign.getId());
                    mail.setRecipientMail(audience.getEmail());
                    mail.setSubject(emailCampaign.getCampaign().getSubject());
                    mail.setStatus(DeliveryStatus.PENDING.name());
                    mail.setSendDate(LocalDate.now());
                    mail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
                    mail.setCheckTimes(0);
                    mails.add(mail);
                }
                try{
                    mailService.insertBatch(mails);
                    mailServerService.sendBatchMails(emailCampaign);
                    updateCampaignExecuteStatus(campaign);
                    log.info("batch insert mail record successfully");
                }catch (Exception e){
                    log.error("batch insert email record fail : "+e.getMessage());
                }
            }
        }
    }

}
