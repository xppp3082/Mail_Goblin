package com.example.personal_project.controller;

import com.example.personal_project.model.Audience;
import com.example.personal_project.service.AudienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.server.ExportException;

@RestController
@Slf4j
@RequestMapping("api/1.0/audience")
public class AudienceController {

    private final AudienceService audienceService;

    public AudienceController(AudienceService audienceService) {
        this.audienceService = audienceService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewAudience(@RequestBody Audience audience) {
        //includes:
        //step1. 新增audience
        //step2. 針對audience中的tagList進行batch insert
        try {
            Audience tagerAudience = audienceService.insertNewAudience(audience);
            audienceService.insertBatchTagAudience(audience);
            return new ResponseEntity<>("Successfully create new audience!", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error on creating new audience in controller layer : " + e.getMessage());
            return new ResponseEntity<>("Error on creating new audience.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAudience(@RequestBody Audience audience) {
        try {
            audienceService.deleteAudience(audience);
            return new ResponseEntity<>("Successfully delete this audience", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error on deleting audience in controller layer : " + e.getMessage());
            return new ResponseEntity<>("Error on deleting audience", HttpStatus.BAD_REQUEST);
        }
    }
}
