package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.component.MailPublisher;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.status.CampaignStatus;
import com.example.personal_project.service.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/1.0/campaign")
public class CampaignController {
    private final AuthenticationComponent authenticationComponent;
    private final CampaignService campaignService;

    private final MailPublisher mailPublisher;

    public CampaignController(AuthenticationComponent authenticationComponent, CampaignService campaignService, MailPublisher mailPublisher) {
        this.authenticationComponent = authenticationComponent;
        this.campaignService = campaignService;
        this.mailPublisher = mailPublisher;
    }

    @GetMapping("/all")
    public ResponseEntity<?>getAllCampaignsByAccount(){
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            List<Campaign> campaigns = campaignService.getAllCampaignsByAccount(account);
            return new ResponseEntity<>(campaigns, HttpStatus.OK);
        }catch (Exception e){
            String errorMessage = "Error on getting campaigns by company account :";
            log.error(errorMessage+e.getMessage());
            return new ResponseEntity<>(errorMessage,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?>addCompleteCampaigns(@RequestBody Campaign campaign){
        try {
            campaign = campaignService.insertNewCampaign(campaign, CampaignStatus.COMPLETED.name());
            return new ResponseEntity<>(campaign,HttpStatus.OK);
        }catch (Exception e){
            String errorMessage = "Error on insert new campaign in controller layer : "+e.getMessage();
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/draft")
    public ResponseEntity<?>addDraftCampaigns(@RequestBody Campaign campaign){
        try {
            campaign = campaignService.insertNewCampaign(campaign, CampaignStatus.DRAFT.name());
            return new ResponseEntity<>(campaign,HttpStatus.OK);
        }catch (Exception e){
            String errorMessage = "Error on insert new campaign in controller layer : "+e.getMessage();
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?>sendCampaignById(@RequestParam("id")Long campaignId){
        try {
//            campaignService.sendCampaignById(campaignId);
            Campaign campaign = campaignService.findCampaignById(campaignId);
            mailPublisher.publishCampaign(campaign);
            String successResponse = "Successfully send campaign with id : "+campaignId;
            return new ResponseEntity<>(successResponse,HttpStatus.OK);
        }catch (Exception e){
            String errorResponse = "Error on sending campaign with id : "+campaignId;
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }
    }
}