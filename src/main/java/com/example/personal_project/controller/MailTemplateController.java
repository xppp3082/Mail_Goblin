package com.example.personal_project.controller;

import com.example.personal_project.model.MailTemplate;
import com.example.personal_project.service.MailTemplateService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("api/1.0/template")
public class MailTemplateController {
    private final MailTemplateService mailTemplateService;
    public MailTemplateController(MailTemplateService mailTemplateService) {
        this.mailTemplateService = mailTemplateService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllTemplateByCompanyID(@RequestParam(value = "id") Long companyId){
        try{
            List<MailTemplate> templates = mailTemplateService.getTemplatesByCompany(companyId);
            return new ResponseEntity<>(templates, HttpStatus.OK);
        }catch (Exception e){
            log.error("error on getting all the template under this company in controller layer");
            return new ResponseEntity<>("Request Error,please check the company of this id exists",HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?>getTemplateByID(@RequestParam(value = "id")Long templateId){
        try{
            MailTemplate mailTemplate = mailTemplateService.findMailTemplateById(templateId);
            return new ResponseEntity<>(mailTemplate,HttpStatus.OK);
        }catch (Exception e){
            log.error("error on finding the template with id : " + templateId);
            return new ResponseEntity<>("Failed on finding the template, please check you request." , HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTemplateByID(@RequestParam(value = "id") Long templateId){
        try{
            mailTemplateService.deleteTemplate(templateId);
            return new ResponseEntity<>("Successfully delete the template.",HttpStatus.OK);
        }catch (Exception e){
            log.error("error on delete the template with id : " + templateId);
            return new ResponseEntity<>("Failed on delete the template, please check you request." , HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateTemplate(@RequestParam(value = "id") Long templateId,@RequestBody MailTemplate mailTemplate){
        try {
            mailTemplate.setId(templateId);
            mailTemplateService.updateTemplate(mailTemplate);
            return new ResponseEntity<>("Successfully update the template.",HttpStatus.OK);
        }catch (Exception e){
            log.error("error on updating the template with id : " + mailTemplate.getId());
            return new ResponseEntity<>("Failed on updating the template, please check you request." , HttpStatus.BAD_REQUEST);
        }
    }
}
