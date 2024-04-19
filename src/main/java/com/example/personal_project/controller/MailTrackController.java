package com.example.personal_project.controller;

import com.example.personal_project.event.MailgunWebhookEvent;
import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.repository.AudienceRepo;
import com.example.personal_project.service.AudienceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public MailTrackController(AudienceService audienceService, ObjectMapper objectMapper) {
        this.audienceService = audienceService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/open")
    public ResponseEntity<?>trackUserOpen(@RequestParam(value = "UID")String userUUID){
        log.info("it's a set of " +userUUID );
        String output = String.format("track %s under open successfully!",userUUID);
        audienceService.updateMailOpen(userUUID);
        log.info(output);
        return new ResponseEntity<>(output,HttpStatus.OK);
    }

    @GetMapping("/click")
    public ResponseEntity<?> trackUserClick(@RequestParam(value = "UID")String userUUID,
                                            @RequestParam(value = "cusWeb",required = false)String customerWebSite,
                                            HttpServletResponse response){
        log.info("it's a set of " +userUUID);
        String output = String.format("track %s under click successfully!",userUUID);
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
