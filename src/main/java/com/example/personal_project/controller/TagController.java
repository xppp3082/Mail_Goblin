package com.example.personal_project.controller;

import com.example.personal_project.model.Tag;
import com.example.personal_project.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/1.0/tag")
public class TagController {
    public final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addNewTag(@RequestBody Tag tag){
        tagService.insertTag(tag);
        return new ResponseEntity<>(
                String.format("Add new tag under the company with id : %s successfully.",tag.getCompanyId()),
                HttpStatus.OK);
    }
}
