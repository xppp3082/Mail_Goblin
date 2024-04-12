package com.example.personal_project.controller;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.repository.AudienceRepo;
import com.example.personal_project.service.AudienceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/1.0/track")
public class MailTrackController {

    private final AudienceService audienceService;

    public MailTrackController(AudienceService audienceService) {
        this.audienceService = audienceService;
    }

    @GetMapping("/test.png")
    public ResponseEntity<?> trackUserTest(@RequestParam(value = "userId")String userId){
        log.info(userId);
        log.info("track "+ userId +" open successfully!");
        return new ResponseEntity<>("track "+ userId +" open successfully!", HttpStatus.OK);
    }

//    @GetMapping("/open")
//    public ResponseEntity<?>trackUserOpen(@RequestParam(value = "UID")String userUUID,
//                                          @RequestParam(value = "CID")String companyUUID){
//        log.info("it's a set of " +userUUID +" and " + companyUUID);
//        String output = String.format("track %s under %s open successfully!",userUUID,companyUUID);
//        audienceService.updateMailOpen(userUUID, companyUUID);
//        log.info(output);
//        return new ResponseEntity<>(output,HttpStatus.OK);
//    }

    @GetMapping("/open")
    public ResponseEntity<?>trackUserOpen(@RequestParam(value = "UID")String userUUID){
        log.info("it's a set of " +userUUID );
        String output = String.format("track %s under %s open successfully!",userUUID);
        audienceService.updateMailOpen(userUUID);
        log.info(output);
        return new ResponseEntity<>(output,HttpStatus.OK);
    }

//    @GetMapping("/click")
//    public ResponseEntity<?> trackUserClick(@RequestParam(value = "UID")String userUUID,
//                                            @RequestParam(value = "CID")String companyUUID,
//                                            @RequestParam(value = "cusWeb",required = false)String customerWebSite,
//                                            HttpServletResponse response){
//        log.info("it's a set of " +userUUID +" and " + companyUUID);
//        String output = String.format("track %s under %s click successfully!",userUUID,companyUUID);
//        log.info(output);
//        if (customerWebSite != null && !customerWebSite.isEmpty()) {
//            try {
//                // 执行重定向到 customerWebSite
//                audienceService.updateMailClick(userUUID, companyUUID);
//                response.sendRedirect(customerWebSite);
//            } catch (IOException e) {
//                log.error("Failed to redirect to customer website: " + e.getMessage());
//                return new ResponseEntity<>("Failed to redirect to customer website", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }
//        return  new ResponseEntity<>(output,HttpStatus.OK);
//    }

    @GetMapping("/click")
    public ResponseEntity<?> trackUserClick(@RequestParam(value = "UID")String userUUID,
                                            @RequestParam(value = "cusWeb",required = false)String customerWebSite,
                                            HttpServletResponse response){
        log.info("it's a set of " +userUUID);
        String output = String.format("track %s under %s click successfully!",userUUID);
        log.info(output);
        if (customerWebSite != null && !customerWebSite.isEmpty()) {
            try {
                // 执行重定向到 customerWebSite
                audienceService.updateMailClick(userUUID);
                response.sendRedirect(customerWebSite);
            } catch (IOException e) {
                log.error("Failed to redirect to customer website: " + e.getMessage());
                return new ResponseEntity<>("Failed to redirect to customer website", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return  new ResponseEntity<>(output,HttpStatus.OK);
    }

    @GetMapping("/tagAudience")
    public ResponseEntity<?>getAllAudienceByCampaign_test(@RequestBody Campaign campaign){
        log.info(campaign.toString());
        try{
            List<Audience> audiences = audienceService.getAllAudienceByCampaign(campaign);
            return  new ResponseEntity<>(audiences,HttpStatus.OK);
        }catch (Exception e){
            log.error(e.getMessage());
            return new ResponseEntity<>("Fail to get all audience from this campaign",HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("/mailgun/webhook")
//    public ResponseEntity<?> handleMailgunWebhook(@RequestBody Mailgun)
}
