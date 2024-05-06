package com.example.personal_project.controller;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.service.impl.CampaignServiceImpl;
import com.example.personal_project.service.impl.MailServerService;
import com.example.personal_project.service.impl.SendMailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("api/1.0/send")
public class MailSendController {

    public final MailServerService mailServerService;
    public final CampaignServiceImpl campaignService;

    public final SendMailService sendMailService;

    public MailSendController(MailServerService mailServerService, CampaignServiceImpl campaignService, SendMailService sendMailService) {
        this.mailServerService = mailServerService;
        this.campaignService = campaignService;
        this.sendMailService = sendMailService;
    }

    @PostMapping("/campaign")
    public ResponseEntity<?> sendCampaign(@RequestBody Campaign campaign) throws MessagingException, UnsupportedEncodingException {
        campaignService.sendCampaign(campaign);
        return new ResponseEntity<>("Campaign send successfully !", HttpStatus.OK);
    }

    @PostMapping("/mailgun")
    public ResponseEntity<?> sendMailByMailgunTest() {
        try {
            log.info("mailgun api test!");
            sendMailService.sendEmail();
            return new ResponseEntity<>("test ok", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
