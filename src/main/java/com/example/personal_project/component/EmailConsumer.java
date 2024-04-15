package com.example.personal_project.component;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.model.Mail;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CampaignService;
import com.example.personal_project.service.MailService;
import com.example.personal_project.service.impl.MailServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@Component
//@Slf4j
//public class EmailConsumer {
//
//    private final AudienceService audienceService;
//    private final MailService mailService;
//    private final MailServerService mailServerService;
//    private final CampaignService campaignService;
//
//    public EmailConsumer(AudienceService audienceService, MailService mailService, MailServerService mailServerService, CampaignService campaignService) {
//        this.audienceService = audienceService;
//        this.mailService = mailService;
//        this.mailServerService = mailServerService;
//        this.campaignService = campaignService;
//    }
//
//    @RabbitListener(queues = "campaignQueue")
//    public void processEmaiRequest(Campaign campaign){
//        //將Campaign轉換成EmailCampaign
//        //處理寄送需求
//        List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
//        EmailCampaign emailCampaign = new EmailCampaign(campaign,audiences);
//        List<Mail>mails = new ArrayList<>();
//        for(Audience audience: emailCampaign.getAudiences()){
//            Mail mail = new Mail();
//            mail.setCompanyID(campaign.getId());
//            mail.setRecipientMail(audience.getEmail());
//            mail.setSubject(emailCampaign.getCampaign().getSubject());
//            mail.setStatus(DeliveryStatus.PENDING.name());
//            mail.setSendDate(LocalDate.now());
//            mail.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
//            mail.setCheckTimes(0);
//            mails.add(mail);
//        }
//        try{
//            mailService.insertBatch(mails);
//            mailServerService.sendBatchMails(emailCampaign);
//            campaignService.updateCampaignExecuteStatus(campaign);
//            log.info("batch insert mail record successfully");
//        }catch (Exception e){
//            log.error("batch insert email record fail : "+e.getMessage());
//        }
//    }
//}
