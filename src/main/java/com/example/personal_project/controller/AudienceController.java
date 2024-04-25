package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.Audience;
import com.example.personal_project.model.response.GenericResponse;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("api/1.0/audience")
public class AudienceController {

    private final AudienceService audienceService;
    private final CompanyService companyService;
    private final AuthenticationComponent authenticationComponent;

    @Value("${product.paging.size}")
    private int pagingSize;

    public AudienceController(AudienceService audienceService, CompanyService companyService, AuthenticationComponent authenticationComponent) {
        this.audienceService = audienceService;
        this.companyService = companyService;
        this.authenticationComponent = authenticationComponent;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAudienceByAccount() {
        //retrieve audience from JWT parser
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Long companyId = companyService.getIdByAccount(account);
            List<Audience> audiences = audienceService.getAudiencesWithTagsByCompanyId(companyId);
//            List<Audience> audiences = audienceService.getAllAudienceByAccount(account);
            return new ResponseEntity<>(audiences, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "error on getting all audience by company account.";
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/tag")
    public ResponseEntity<?> getAllAudienceByTag(@RequestParam("id") Long tagId) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            List<Audience> audiences = audienceService.getAudienceByTag(tagId, account);
            return new ResponseEntity<>(audiences, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "error on getting all audience by tag.";
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/new-count")
    public ResponseEntity<?> getNewAudienceCountLast7Days(@RequestParam("days") Integer days) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Map<String, Integer> resultMap = audienceService.getNewAudienceCountLastWeek(account, days);
            return new ResponseEntity<>(resultMap, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "error on getting audience been added on last 7 days.";
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchAudienceByAccountANDMail(@RequestParam("keyword") String keyword) {
        //retrieve audience from JWT parser
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Long companyId = companyService.getIdByAccount(account);
            List<Audience> audiences = audienceService.searchAudiencesWithTagsByCompanyIdANDMail(companyId, keyword);
            return new ResponseEntity<>(audiences, HttpStatus.OK);
        } catch (Exception e) {
            String errorMessage = "error on searching audiences by company account and keyword.";
            log.error(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewAudience(@RequestBody Audience audience) {
        //includes:
        //step1. 新增audience
        //step2. 針對audience中的tagList進行batch insert
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            Long companyId = companyService.getIdByAccount(account);
            audience.setCompanyId(companyId);
            Audience tagerAudience = audienceService.insertNewAudience(audience);
            audienceService.insertBatchTagAudience(audience);
            return new ResponseEntity<>("Successfully create new audience!", HttpStatus.OK);
        } catch (DuplicateKeyException e) {
            log.error("Duplicate account error on creating new audience under this company: " + e.getMessage());
            return new ResponseEntity<>("Audience already exists.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error on creating new audience in controller layer : " + e.getMessage());
            return new ResponseEntity<>("Error on creating new audience.", HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateAudience(@RequestBody Audience audience) {
        try {
            audience = audienceService.updateAudience(audience);
            return new ResponseEntity<>(audience, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error on updating audience in controller layer : " + e.getMessage());
            return new ResponseEntity<>("Error on updating audience.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAudience(@RequestParam("id") Long id) {
        try {
            audienceService.deleteAudience(id);
            return new ResponseEntity<>("Successfully delete this audience", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error on deleting audience in controller layer : " + e.getMessage());
            return new ResponseEntity<>("Error on deleting audience", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getPageAudienceByAccount(@RequestParam("number") Optional<Integer> paging) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            List<Audience> audiences = audienceService.getPageAudienceWithTagsByAccount(account, paging.orElse(0));
            log.info(String.valueOf(audiences.size()));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(audiences.stream().limit(pagingSize).toList(),
                            audiences.size() > pagingSize ? paging.orElse(0) + 1 : null));
        } catch (Exception e) {
            String errorMessage = "Error on getting paging audience by company account :";
            log.error(errorMessage + e.getMessage());
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
