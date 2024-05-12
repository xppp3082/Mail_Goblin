package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.Mail;
import com.example.personal_project.model.MailHook;
import com.example.personal_project.model.RedisMail;
import com.example.personal_project.model.response.GenericResponse;
import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.service.MailService;
import com.example.personal_project.service.impl.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/1.0/track")
public class MailTrackController {

    private final AudienceService audienceService;
    private final ObjectMapper objectMapper;
    private final MailService mailService;
    private final CompanyService companyService;
    private final AuthenticationComponent authenticationComponent;

    private final RedisService redisService;

    @Value("${product.paging.size}")
    private int pagingSize;


    public MailTrackController(AudienceService audienceService, ObjectMapper objectMapper, MailService mailService, CompanyService companyService, AuthenticationComponent authenticationComponent, RedisService redisService) {
        this.audienceService = audienceService;
        this.objectMapper = objectMapper;
        this.mailService = mailService;
        this.companyService = companyService;
        this.authenticationComponent = authenticationComponent;
        this.redisService = redisService;
    }

    @GetMapping("/open")
    public ResponseEntity<?> trackUserOpen(@RequestParam(value = "UID") String userUUID,
                                           @RequestParam(value = "CID") String campaignID,
                                           @RequestParam(value = "recipient") String email,
                                           @RequestParam(value = "subject") String subject) {
        try {
            String output = String.format("track %s under open successfully with campaignId : %s", userUUID, campaignID);
            audienceService.updateMailOpen(userUUID);
            mailService.insertOpenRecord(campaignID, DeliveryStatus.OPEN.name(), userUUID, email, subject);
            return new ResponseEntity<>(output, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "Error on tracking audience open record :";
            log.error(errorMessage + e.getMessage());
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/click")
    public ResponseEntity<?> trackUserClick(@RequestParam(value = "UID") String userUUID,
                                            @RequestParam(value = "CID") String campaignID,
                                            @RequestParam(value = "cusWeb", required = false) String customerWebSite,
                                            @RequestParam(value = "recipient") String email,
                                            @RequestParam(value = "subject") String subject,
                                            HttpServletResponse response) {
        log.info("it's a set of " + userUUID);
        String output = String.format("track %s under click successfully!", userUUID);
        log.info(output);
        if (customerWebSite != null && !customerWebSite.isEmpty()) {
            try {
                // 执行重定向到 customerWebSite
                audienceService.updateMailClick(userUUID);
                mailService.insertOpenRecord(campaignID, DeliveryStatus.CLICK.name(), userUUID, email, subject);
                response.sendRedirect(customerWebSite);
            } catch (IOException e) {
                log.error("Failed to redirect to customer website: " + e.getMessage());
                return new ResponseEntity<>("Failed to redirect to customer website", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(output, HttpStatus.OK);
    }

    @GetMapping("/delivery")
    public ResponseEntity<?> trackDeliveryRate() {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
//            Long companyId = companyService.getIdByAccount(account);
            Double successRate = mailService.getMailDeliveryRateForCompany(account);
            return new ResponseEntity<>(successRate, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting delivery rate of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/daily-delivery")
    public ResponseEntity<?> dailyMailDeliveryRate() {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
//            Long companyId = companyService.getIdByAccount(account);
//            Map<LocalDate,Double> successRate = mailService.getDailyMailDeliveryRate(companyId);
            Map<LocalDate, Double> successRate = mailService.trackDailyMailDeliveryRate(account);
            return new ResponseEntity<>(successRate, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting daily delivery rate of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/date-delivery")
    public ResponseEntity<?> dailyMailDeliveryRateByDate(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                         @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<LocalDate, Double> successRate = mailService.trackDailyMailDeliveryRateByDate(account, startDate, endDate);
            return new ResponseEntity<>(successRate, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting daily delivery rate of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/conversion")
    public ResponseEntity<?> calculateMailConversionRate() {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<String, Integer> conversionRates = mailService.calculateMailConversionRate(account);
            return new ResponseEntity<>(conversionRates, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting conversion rate of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/date-conversion")
    public ResponseEntity<?> calculateMailConversionRateByDate(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                               @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
//            Map<String, Integer> conversionRates = mailService.calculateMailConversionRate(account);
            Map<String, Integer> conversionRates = mailService.calculateMailConversionRateByDate(account, startDate, endDate);
            return new ResponseEntity<>(conversionRates, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting conversion rate of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/daily-event")
    public ResponseEntity<?> dailyEventCount(@RequestParam("days") Integer days) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<String, Map<LocalDate, Integer>> result = mailService.analyzeEventPastDays(account, days);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting daily event count of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/date-event")
    public ResponseEntity<?> dailyEventCountByDate(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<String, Map<LocalDate, Integer>> result = mailService.analyzeEventPastByDate(account, startDate, endDate);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting daily event count of company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/campaign-age")
    public ResponseEntity<?> analyzeCampaignAudienceByAge(@RequestParam("id") Long campaignId) {
        try {
            List<Map<String, Object>> resultList = mailService.analyzeCampaignAudienceByAge(campaignId);
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on analyzing audience age of this campaign.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/failed")
    public ResponseEntity<?> trackFailedMailsByCampaign(@RequestParam("id") Long campaignId) {
        try {
            List<Mail> failMails = mailService.trackFailedMailsByCampaignId(campaignId);
            return new ResponseEntity<>(failMails, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting failed mails by campaign.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/failed-paging")
    public ResponseEntity<?> trackPageFailedMailsByCampaign(@RequestParam("id") Long campaignId,
                                                            @RequestParam("number") Optional<Integer> paging) {
        try {
            List<Mail> failMails = mailService.trackFailedMailsByCampaignIdWithPage(campaignId, paging.orElse(0));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(failMails.stream().limit(pagingSize).toList(),
                            failMails.size() > pagingSize ? paging.orElse(0) + 1 : null
                    ));
        } catch (Exception e) {
            String errorResponse = "Error on getting mails event by campaign.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPageEventMailsByCampaign(@RequestParam("keyword") String keyword,
                                                            @RequestParam("id") Long campaignId,
                                                            @RequestParam("number") Optional<Integer> paging) {
        try {
            List<Mail> keywordResults = mailService.searchMailsByKeywordWithPage(keyword, campaignId, paging.orElse(0));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(keywordResults.stream().limit(pagingSize).toList(),
                            keywordResults.size() > pagingSize ? paging.orElse(0) + 1 : null
                    ));
        } catch (Exception e) {
            String errorResponse = "Error on  search keyword by campaign.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/mailgun/webhook")
    public ResponseEntity<?> handleMailgunWebhook(@RequestBody String payload) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(payload);
        log.info(rootNode.toString());
        String eventType = rootNode.path("event-data").path("event").asText();
        String recipientEmail = rootNode.path("event-data").path("recipient").asText();
        String subject = rootNode.path("event-data").path("message").path("headers").path("subject").asText();
        String mimeId = rootNode.path("event-data").path("message").path("headers").path("message-id").asText();
        log.info(recipientEmail);
        log.info(eventType);
        log.info(mimeId);
        String status = "";
        if (eventType.equals("failed")) {
            status = DeliveryStatus.FAILED.name();
            log.error("Mail Error : Failed to send mail to : " + recipientEmail +
                    "with campaign subject : " + subject);
        } else {
            status = DeliveryStatus.RECEIVE.name();
        }
        MailHook mailHook = new MailHook(recipientEmail, subject, mimeId, status);
        mailHook.setMimeID(mimeId);
        RedisMail redisMail = new RedisMail();
        redisMail.setRecipientMail(recipientEmail);
        redisMail.setSubject(subject);
        redisMail.setMimeID(mimeId);
        redisMail.setStatus(status);
        System.out.println(mailHook.getMimeID());
        try {
//            mailService.insertReceiveRecordWithMailHook(mailHook);
            redisService.updateMailFromWebhook(redisMail);
            return new ResponseEntity<>(mailHook, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "error on insert record with mailgun webhook";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
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
//        return new ResponseEntity<>("Email send to " + recipientEmail + "failed.", HttpStatus.OK);
    }
}
