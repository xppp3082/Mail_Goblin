package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.Tag;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/1.0/tag")
public class TagController {
    public final TagService tagService;
    private final AuthenticationComponent authenticationComponent;
    private final CompanyService companyService;

    public TagController(TagService tagService, AuthenticationComponent authenticationComponent, CompanyService companyService) {
        this.tagService = tagService;
        this.authenticationComponent = authenticationComponent;
        this.companyService = companyService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewTag(@RequestBody Tag tag){
        String account = authenticationComponent.getAccountFromAuthentication();
        Long companyId = companyService.getIdByAccount(account);
        tag.setCompanyId(companyId);
        tagService.insertTag(tag);
        return new ResponseEntity<>(
                String.format("Add new tag under the company with id : %s successfully.",tag.getCompanyId()),
                HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTag(@RequestParam("id") Long tagId){
        String account = authenticationComponent.getAccountFromAuthentication();
        Long companyId = companyService.getIdByAccount(account);
        tagService.deleteTag(tagId,companyId);
        return new ResponseEntity<>(
                String.format("Delete tag under the company with id : %s successfully.",companyId),
                HttpStatus.OK);
    }

    //少一層再去找CompanyId的步驟，直接join速度最快
    @GetMapping("/get")
    public ResponseEntity<?>getAllTagsByCompany(){
        String account = authenticationComponent.getAccountFromAuthentication();
        List<Tag> tags= tagService.getTagsByCompanyAccount(account);
        return new ResponseEntity<>(tags, HttpStatus.OK);
    }
}
