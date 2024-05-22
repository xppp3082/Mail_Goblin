package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.model.Tag;
import com.example.personal_project.model.response.GenericResponse;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("api/1.0/tag")
public class TagController {
    public final TagService tagService;
    private final AuthenticationComponent authenticationComponent;
    private final CompanyService companyService;
    @Value("${product.paging.size}")
    private int pagingSize;

    public TagController(TagService tagService, AuthenticationComponent authenticationComponent, CompanyService companyService) {
        this.tagService = tagService;
        this.authenticationComponent = authenticationComponent;
        this.companyService = companyService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewTag(@RequestBody Tag tag) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            Long companyId = companyService.getIdByAccount(account);
            tag.setCompanyId(companyId);
            tagService.insertTag(tag);
            return new ResponseEntity<>(
                    String.format("Add new tag under the company with id : %s successfully.", tag.getCompanyId()),
                    HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on adding tag to DB.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteTag(@RequestParam("id") Long tagId) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            Long companyId = companyService.getIdByAccount(account);
            tagService.deleteTag(tagId, companyId);
            return new ResponseEntity<>(
                    String.format("Delete tag under the company with id : %s successfully.", companyId),
                    HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on deleting tag from DB.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/get")
    public ResponseEntity<?> getAllTagsByCompany() {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            List<Tag> tags = tagService.getTagsByCompanyAccount(account);
            return new ResponseEntity<>(tags, HttpStatus.OK);
        } catch (Exception e) {
            String errorResponse = "Error on getting all tags by company.";
            log.error(errorResponse + " : " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getPageTagsByAccount(@RequestParam("number") Optional<Integer> paging) {
        try {
            String account = authenticationComponent.getAccountFromAuthentication();
            if (account == null) {
                String errorMessage = "User is not authenticated.";
                log.warn(errorMessage);
                return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
            }
            List<Tag> tags = tagService.getPageTagsByCompanyAccount(account, paging.orElse(0));
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(tags.stream().limit(pagingSize).toList(),
                            tags.size() > pagingSize ? paging.orElse(0) + 1 : null));
        } catch (Exception e) {
            String errorMessage = "Error on getting paging tags by company account :";
            log.error(errorMessage + e.getMessage());
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }
    }
}
