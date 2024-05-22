package com.example.personal_project.service;

import com.example.personal_project.model.Tag;

import java.util.List;

public interface TagService {
    void insertTag(Tag tag);

    void deleteTag(Long tagId, Long companyId);

    List<Tag> getTagsByCompany(Long companyId);

    List<Tag> getTagsByCompanyAccount(String account);

    List<Tag> getPageTagsByCompanyAccount(String account, int paging);
}
