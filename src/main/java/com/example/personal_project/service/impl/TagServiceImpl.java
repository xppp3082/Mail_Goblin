package com.example.personal_project.service.impl;

import com.example.personal_project.model.Tag;
import com.example.personal_project.repository.TagRepo;
import com.example.personal_project.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final TagRepo tagRepo;

    public TagServiceImpl(TagRepo tagRepo) {
        this.tagRepo = tagRepo;
    }

    @Override
    public void insertTag(Tag tag) {
        tagRepo.insertTag(tag);
    }
}
