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
//@RequestMapping("/marketing")
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
        model.addAttribute("message");
        model.addAttribute("error");
        model.addAttribute("mailTemplate",new MailTemplate());
        return "marketing_form";
    }

    @PostMapping("/saveTemplate")
    public String saveTemplate(@ModelAttribute MailTemplate mailTemplate,
                               RedirectAttributes redirectAttributes){
        try{
            mailTemplateService.insertNewTemplate(mailTemplate);
            redirectAttributes.addFlashAttribute("message", "Successful save the mail template!");
        }catch (Exception e){
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Errors on saving the mail template!");
        }
        return "redirect:/marketing/configure";
    }

    @GetMapping("/templates")
    public String showAlltemplate(){
        return "templateList";
    }
}
