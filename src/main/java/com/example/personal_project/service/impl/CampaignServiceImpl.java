package com.example.personal_project.service.impl;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.model.Mail;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CampaignServiceImpl implements CampaignService {
    private final MailServerService mailServerService;
    private final MailServiceImpl mailService;
    private final AudienceServiceImpl audienceService;
    public CampaignServiceImpl(MailServerService mailServerService, MailServiceImpl mailService, AudienceServiceImpl audienceService) {
        this.mailServerService = mailServerService;
        this.mailService = mailService;
        this.audienceService = audienceService;
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
}
