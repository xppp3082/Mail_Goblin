package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.MailTemplate;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.service.MailTemplateService;
import com.example.personal_project.service.StorageService;
import com.example.personal_project.service.impl.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
//@RequestMapping("/marketing")
public class FrontController {
    public final MailTemplateService mailTemplateService;
    public final StorageService storageService;
    public final S3Service s3Service;
    private final AuthenticationComponent authenticationComponent;
    private final CompanyService companyService;

    public FrontController(MailTemplateService mailTemplateService, StorageService storageService, S3Service s3Service, AuthenticationComponent authenticationComponent, CompanyService companyService) {
        this.mailTemplateService = mailTemplateService;
        this.storageService = storageService;
        this.s3Service = s3Service;
        this.authenticationComponent = authenticationComponent;
        this.companyService = companyService;
    }

    @GetMapping("/configure")
    public String showMarketingForm(Model model) {
        model.addAttribute("message");
        model.addAttribute("error");
        model.addAttribute("mailTemplate", new MailTemplate());
        return "marketing_form";
    }

    @PostMapping("/saveTemplate")
    public String saveTemplate(@ModelAttribute MailTemplate mailTemplate,
                               RedirectAttributes redirectAttributes) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            mailTemplateService.insertNewTemplate(account, mailTemplate);
            redirectAttributes.addFlashAttribute("message", "Successful save the mail template!");
        } catch (Exception e) {
            log.error(e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Errors on saving the mail template!");
        }
        return "redirect:/configure";
    }

    @GetMapping("/templates")
    public String showAlltemplate() {
        return "templateList";
    }

    @GetMapping("/templateEditor")
    public String editTemplate(@RequestParam("id") Long templateId) {
        return "templateEditor";
    }

    @GetMapping("/homePage")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/audience")
    public String showAudiencePage() {
        return "audience";
    }

    @GetMapping("/dashboard")
    public String showDashboardPage() {
        return "dashboard";
    }
}
