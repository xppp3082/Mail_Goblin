package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.MailTemplate;
import com.example.personal_project.model.response.GenericResponse;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.service.MailTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("api/1.0/template")
public class MailTemplateController {
    private final MailTemplateService mailTemplateService;
    private final CompanyService companyService;
    private final AuthenticationComponent authenticationComponent;

    @Value("${product.paging.size}")
    private int pagingSize;

    public MailTemplateController(MailTemplateService mailTemplateService, CompanyService companyService, AuthenticationComponent authenticationComponent) {
        this.mailTemplateService = mailTemplateService;
        this.companyService = companyService;
        this.authenticationComponent = authenticationComponent;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTemplateByCompanyAccount() {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            List<MailTemplate> templates = mailTemplateService.getTemplateByAccount(account);
            return new ResponseEntity<>(templates, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error on getting all the template under this company in controller layer");
            return new ResponseEntity<>("Request Error,please check the company of this id exists", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> getTemplateByID(@RequestParam(value = "id") Long templateId) {
        try {
            MailTemplate mailTemplate = mailTemplateService.findMailTemplateById(templateId);
            return new ResponseEntity<>(mailTemplate, HttpStatus.OK);
        } catch (Exception e) {
            log.error("error on finding the template with id : " + templateId);
            return new ResponseEntity<>("Failed on finding the template, please check you request.", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTemplateByID(@RequestParam(value = "id") Long templateId) {
        try {
            mailTemplateService.deleteTemplate(templateId);
            return new ResponseEntity<>("Successfully delete the template.", HttpStatus.OK);
        } catch (Exception e) {
            log.error("error on delete the template with id : " + templateId);
            return new ResponseEntity<>("Failed on delete the template, please check you request.", HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateTemplate(@RequestParam(value = "id") Long templateId, @RequestBody MailTemplate mailTemplate) {
        try {
            mailTemplate.setId(templateId);
            mailTemplateService.updateTemplate(mailTemplate);
            return new ResponseEntity<>("Successfully update the template.", HttpStatus.OK);
        } catch (Exception e) {
            log.error("error on updating the template with id : " + mailTemplate.getId());
            return new ResponseEntity<>("Failed on updating the template, please check you request.", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTemplate(@RequestBody MailTemplate mailTemplate) {
        try {
            log.info("Adding tempalate via endpoint!");
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            MailTemplate targetTemplate = mailTemplateService.insertNewTemplateWithAccount(account, mailTemplate);
            return new ResponseEntity<>(targetTemplate, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on creating new template by company";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getPageTemplateByAccount(@RequestParam("number") Optional<Integer> paging) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            List<MailTemplate> mailTemplates = mailTemplateService.getPageMailTemplateByCompany(account, paging.orElse(0));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(mailTemplates.stream().limit(pagingSize).toList(),
                            mailTemplates.size() > pagingSize ? paging.orElse(0) + 1 : null
                    ));
        } catch (Exception e) {
            String errorMessage = "Error on getting paging templates by company account :";
            log.error(errorMessage + e.getMessage());
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
