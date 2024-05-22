package com.example.personal_project.service.impl;

import com.example.personal_project.model.Tag;
import com.example.personal_project.repository.TagRepo;
import com.example.personal_project.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TagServiceImpl implements TagService {

    private final TagRepo tagRepo;

    @Value("${product.paging.size}")
    private int pagingSize;

    public TagServiceImpl(TagRepo tagRepo) {
        this.tagRepo = tagRepo;
    }

    @Override
    public void insertTag(Tag tag) {
        tagRepo.insertTag(tag);
    }

    @Override
    public void deleteTag(Long tagId, Long companyId) {
        tagRepo.deleteTag(tagId, companyId);
    }

    @Override
    public List<Tag> getTagsByCompany(Long companyId) {
        return tagRepo.getAllTagsByCompanyId(companyId);
    }

    @Override
    public List<Tag> getTagsByCompanyAccount(String account) {
        return tagRepo.getAllTagsByCompanyAccount(account);
    }

    @Override
    public List<Tag> getPageTagsByCompanyAccount(String account, int paging) {
        int offset = paging * pagingSize;
        return tagRepo.getPageTagsByCompanyAccount(account, pagingSize + 1, offset);
    }
}
