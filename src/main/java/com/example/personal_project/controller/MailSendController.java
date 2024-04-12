package com.example.personal_project.controller;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.model.EmailCampaign;
import com.example.personal_project.service.impl.CampaignServiceImpl;
import com.example.personal_project.service.impl.MailServerService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@Slf4j
@RestController
@RequestMapping("api/1.0/send")
public class MailSendController {

    public final MailServerService mailServerService;
    public final CampaignServiceImpl campaignService;

    public MailSendController(MailServerService mailServerService, CampaignServiceImpl campaignService) {
        this.mailServerService = mailServerService;
        this.campaignService = campaignService;
    }

    @PostMapping("/test")
    public ResponseEntity<?> testEndPoint(@RequestBody EmailCampaign emailCampaign){
        log.info(DeliveryStatus.OPEN.name());
        return new ResponseEntity<>(emailCampaign, HttpStatus.OK);
    }

    @PostMapping("/campaign")
    public ResponseEntity<?> sendCampaign(@RequestBody Campaign campaign) throws MessagingException, UnsupportedEncodingException {
        campaignService.sendCampaign(campaign);
        return new ResponseEntity<>("Campaign send successfully !", HttpStatus.OK);
    }

}
