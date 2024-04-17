package com.example.personal_project.controller;

import com.example.personal_project.model.MailTemplate;
import com.example.personal_project.service.MailTemplateService;
import com.example.personal_project.service.StorageService;
import com.example.personal_project.service.impl.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/marketing")
public class FrontController {
    public final MailTemplateService mailTemplateService;
    public final StorageService storageService;
    public final S3Service s3Service;
    public FrontController(MailTemplateService mailTemplateService, StorageService storageService, S3Service s3Service) {
        this.mailTemplateService = mailTemplateService;
        this.storageService = storageService;
        this.s3Service = s3Service;
    }

    @GetMapping("/configure")
    public String showMarketingForm(Model model){
        model.addAttribute("mailTemplate",new MailTemplate());
        return "marketing_form";
    }

//    @PostMapping("/configure")
//    public String configureMarketing(String marketingUrl, String marketingSubject, Model model) {
//        // 將行銷網址和主題放入模型中，以便在模板中使用
//        model.addAttribute("marketingUrl", marketingUrl);
//        model.addAttribute("marketingSubject", marketingSubject);
//        return "marketing_confirmation";
//    }

    @PostMapping("/saveTemplate")
    public String saveTemplate(@ModelAttribute MailTemplate mailTemplate,
                               @RequestParam("file")MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes){
        String uploadDir = "/mail_campaigns/";
        String folderName = "mail_campaigns";
        try{
            log.info(mailTemplate.toString());
            //save mail template via template service.
            String campaignURL = storageService.store(multipartFile,uploadDir);
            campaignURL = campaignURL.replace("\\","/");

            mailTemplate.setPicture(campaignURL);
            mailTemplateService.insertNewTemplate(mailTemplate);
            String keyName= campaignURL.substring(1);
            log.info(keyName);
            s3Service.getBucketsName(s3Service.createS3Client());
            s3Service.uploadObject(keyName,multipartFile);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return "redirect:/marketing/configure";
    }
}
