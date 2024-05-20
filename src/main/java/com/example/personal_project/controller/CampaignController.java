package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.component.MailPublisher;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.response.GenericResponse;
import com.example.personal_project.model.status.CampaignStatus;
import com.example.personal_project.service.CampaignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/1.0/campaign")
public class CampaignController {
    private final AuthenticationComponent authenticationComponent;
    private final CampaignService campaignService;

    private final MailPublisher mailPublisher;

    @Value("${product.paging.size}")
    private int pagingSize;

    public CampaignController(AuthenticationComponent authenticationComponent, CampaignService campaignService, MailPublisher mailPublisher) {
        this.authenticationComponent = authenticationComponent;
        this.campaignService = campaignService;
        this.mailPublisher = mailPublisher;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllCampaignsByAccount() {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            List<Campaign> campaigns = campaignService.getAllCampaignsByAccount(account);
            return new ResponseEntity<>(campaigns, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Error on getting campaigns by company account :";
            log.error(errorMessage + e.getMessage());
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getPageCampaignByAccount(@RequestParam("number") Optional<Integer> paging) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            List<Campaign> campaigns = campaignService.getPageCampaignByAccountWithTag(account, paging.orElse(0));
            int totalCount = campaignService.getTotalCampaignCountByAccount(account);
            int totalPaging = (int) Math.ceil((double) totalCount / pagingSize);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(campaigns.stream().limit(pagingSize).toList(),
                            campaigns.size() > pagingSize ? paging.orElse(0) + 1 : null, totalPaging));
        } catch (Exception e) {
            String errorMessage = "Error on getting paging campaigns by company account :";
            log.error(errorMessage + e.getMessage());
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<?> addCompleteCampaigns(@RequestBody Campaign campaign) {
        log.info("Controller: " + campaign.toString());
        try {
            campaign = campaignService.insertNewCampaign(campaign, CampaignStatus.COMPLETED.name());
            return new ResponseEntity<>(campaign, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Error on insert new campaign in controller layer : " + e.getMessage();
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/draft")
    public ResponseEntity<?> addDraftCampaigns(@RequestBody Campaign campaign) {
        try {
            campaign = campaignService.insertNewCampaign(campaign, CampaignStatus.DRAFT.name());
            return new ResponseEntity<>(campaign, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Error on insert new campaign in controller layer : " + e.getMessage();
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendCampaignById(@RequestParam("id") Long campaignId) {
        try {
            Campaign campaign = campaignService.findCampaignById(campaignId);
            mailPublisher.publishCampaign(campaign);
            String successResponse = "Successfully send campaign with id : " + campaignId;
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on sending campaign with id : " + campaignId;
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCampaignById(@RequestParam("id") Long campaignId) {
        try {
            campaignService.deleteCampaign(campaignId);
            String successResponse = "Successfully delete campaign with id : " + campaignId;
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on deleting campaign with id : " + campaignId;
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
