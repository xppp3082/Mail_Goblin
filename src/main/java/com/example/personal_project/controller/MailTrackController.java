package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.repository.AudienceRepo;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.service.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/1.0/track")
public class MailTrackController {

    private final AudienceService audienceService;
    private final ObjectMapper objectMapper;
    private final MailService mailService;
    private final CompanyService companyService;
    private final AuthenticationComponent authenticationComponent;


    public MailTrackController(AudienceService audienceService, ObjectMapper objectMapper, MailService mailService, CompanyService companyService, AuthenticationComponent authenticationComponent) {
        this.audienceService = audienceService;
        this.objectMapper = objectMapper;
        this.mailService = mailService;
        this.companyService = companyService;
        this.authenticationComponent = authenticationComponent;
    }

    @GetMapping("/open")
    public ResponseEntity<?>trackUserOpen(@RequestParam(value = "UID")String userUUID,
                                          @RequestParam(value = "CID")String campaignID){
        try {
            log.info("it's a set of " +userUUID );
            String output = String.format("track %s under open successfully with campaignId : %s",userUUID,campaignID);
            log.info(output);
            audienceService.updateMailOpen(userUUID);
            mailService.insertOpenRecord(campaignID, DeliveryStatus.OPEN.name(),userUUID);
            return new ResponseEntity<>(output,HttpStatus.OK);
        }catch (Exception e){
            String errorMessage = "Error on tracking audience open record :";
            log.error(errorMessage+e.getMessage());
            return new ResponseEntity<>(errorMessage,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/click")
    public ResponseEntity<?> trackUserClick(@RequestParam(value = "UID")String userUUID,
                                            @RequestParam(value = "CID")String campaignID,
                                            @RequestParam(value = "cusWeb",required = false)String customerWebSite,
                                            HttpServletResponse response){
        log.info("it's a set of " +userUUID);
        String output = String.format("track %s under click successfully!",userUUID);
        log.info(output);
        if (customerWebSite != null && !customerWebSite.isEmpty()) {
            try {
                // 执行重定向到 customerWebSite
                audienceService.updateMailClick(userUUID);
                mailService.insertOpenRecord(campaignID, DeliveryStatus.CLICK.name(),userUUID);
                response.sendRedirect(customerWebSite);
            } catch (IOException e) {
                log.error("Failed to redirect to customer website: " + e.getMessage());
                return new ResponseEntity<>("Failed to redirect to customer website", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return  new ResponseEntity<>(output,HttpStatus.OK);
    }

    @GetMapping("/delivery")
    public  ResponseEntity<?> trackDeliveryRate(){
        try{
            String account = authenticationComponent.getAccountFromAuthentication();
//            Long companyId = companyService.getIdByAccount(account);
            Double successRate = mailService.getMailDeliveryRateForCompany(account);
            return new ResponseEntity<>(successRate,HttpStatus.OK);
        }catch (Exception e){
            String errorResponse = "Error on getting delivery rate of company.";
            log.error(errorResponse+" : "+e.getMessage());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/daily-delivery")
    public ResponseEntity<?> dailyMailDeliveryRate(){
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
//            Long companyId = companyService.getIdByAccount(account);
//            Map<LocalDate,Double> successRate = mailService.getDailyMailDeliveryRate(companyId);
            Map<LocalDate,Double> successRate = mailService.trackDailyMailDeliveryRate(account);
            return new ResponseEntity<>(successRate, HttpStatus.OK);
        }catch (Exception e){
            String errorResponse = "Error on getting daily delivery rate of company.";
            log.error(errorResponse+" : "+e.getMessage());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/conversion")
    public ResponseEntity<?>calculateMailConversionRate(){
        try{
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<String,Integer> conversionRates =mailService.calculateMailConversionRate(account);
            return new ResponseEntity<>(conversionRates, HttpStatus.OK);
        }catch (Exception e){
            String errorResponse = "Error on getting conversion rate of company.";
            log.error(errorResponse+" : "+e.getMessage());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/daily-event")
    public ResponseEntity<?> dailyEventCount(@RequestParam("days")Integer days){
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<String,Map<LocalDate,Integer>> result = mailService.analyzeEventPastDays(account,days);
            return new ResponseEntity<>(result,HttpStatus.OK);
        }catch (Exception e){
            String errorResponse = "Error on getting daily event count of company.";
            log.error(errorResponse+" : "+e.getMessage());
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping("/mailgun/webhook")
//    public ResponseEntity<?> handleMailgunWebhook(@RequestBody String payload) throws JsonProcessingException {
//        JsonNode rootNode = objectMapper.readTree(payload);
//        String eventType = rootNode.path("event-data").path("event").asText();
//        String recipientEmail = rootNode.path("event-data").path("recipient").asText();
//        log.info(recipientEmail);
//        log.info(eventType);
//        if(eventType.equals("delivered")){
//            //update user delivery
//            try{
//                //應該再多一層是對公司id的雙重篩選，因為同一個customer有可能兩間公司都有
//                Audience targetAudience = audienceService.findAudienceByEmail(recipientEmail);
//                audienceService.updateMailCount(targetAudience.getAudienceUUID());
//                log.info(targetAudience.getName());
//                log.info("Track email count successfully!");
//            }catch (Exception e){
//                log.error("Error on tracking mail count" +e.getMessage());
//            }
//        }
//        return  new ResponseEntity<>("Email send to "+recipientEmail+ "failed.",HttpStatus.OK);
//    }
}
