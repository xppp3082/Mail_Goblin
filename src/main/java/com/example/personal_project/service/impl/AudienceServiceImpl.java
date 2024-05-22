package com.example.personal_project.service.impl;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.form.AudienceUpdateForm;
import com.example.personal_project.repository.AudienceRepo;
import com.example.personal_project.service.AudienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AudienceServiceImpl implements AudienceService {
    public final AudienceRepo audienceRepo;

    @Value("${product.paging.size}")
    private int pagingSize;

    public AudienceServiceImpl(AudienceRepo audienceRepo) {
        this.audienceRepo = audienceRepo;
    }

    @Override
    public List<Audience> getAllAudienceByAccount(String account) {
        return audienceRepo.getAllAudienceByAccount(account);
    }

    @Override
    public List<Audience> getAudiencesWithTagsByCompanyId(Long companyId) {
        return audienceRepo.getAudiencesWithTagsByCompanyId(companyId);
    }

    @Override
    public List<Audience> getPageAudienceWithTagsByAccount(String account, int paging) {
        int offset = paging * pagingSize;
        return audienceRepo.getPageAudienceWithTagsByAccount(account, pagingSize + 1, offset);
    }

    @Override
    public List<Audience> searchAudiencesWithTagsByCompanyIdANDMail(Long companyId, String keyword) {
        return audienceRepo.searchAudiencesWithTagsByCompanyIdANDMail(companyId, keyword);
    }

    @Override
    public Audience findAudienceWithTagsById(Long audienceId) {
        return audienceRepo.findAudienceWithTagsById(audienceId);
    }

    @Override
    public Audience updateAudience(Audience audience) {
        return audienceRepo.updateAudience(audience);
    }

    @Override
    public void updateAudienceWithTags(Long audienceId, AudienceUpdateForm audienceUpdateForm) {
        audienceRepo.updateAudienceWithTags(audienceId, audienceUpdateForm);
    }

    @Override
    public void updateMailCount(String audienceUUID, String account) {
        audienceRepo.updateUserMailCount(audienceUUID, account);
    }

    @Override
    public void updateMailOpen(String audienceUUID, String companyUUID) {
        audienceRepo.updateUserMailOpen(audienceUUID, companyUUID);
    }

    @Override
    public void updateMailOpen(String audienceUUID) {
        audienceRepo.updateUserMailOpen(audienceUUID);
    }

    @Override
    public void updateMailClick(String audienceUUID, String companyUUID) {
        audienceRepo.updateUserMailClick(audienceUUID, companyUUID);
    }

    @Override
    public void updateMailClick(String audienceUUID) {
        audienceRepo.updateUserMailClick(audienceUUID);
    }

    @Override
    public void updateMailCount(String audienceUUID) {
        audienceRepo.updateUserMailCount(audienceUUID);
    }

    @Override
    public Audience findAudienceByEmail(String email) {
        return audienceRepo.finAudienceByEmail(email);
    }

    @Override
    public List<Audience> getAllAudienceByCampaign(Campaign campaign) {
        return audienceRepo.getAllAudienceByCampaign(campaign);
    }

    @Override
    public List<Audience> getAudienceByTag(Long tagId, String account) {
        return audienceRepo.getAllAudienceByTagId(tagId, account);
    }

    @Override
    public Map<String, Integer> getNewAudienceCountLastWeek(String account, Integer daysCount) {
        return audienceRepo.getNewAudienceCountLastWeek(account, daysCount);
    }

    @Override
    public Audience insertNewAudience(Audience audience) {
        UUID uuid = UUID.randomUUID();
        audience.setAudienceUUID(uuid.toString());
        return audienceRepo.insertNewAudience(audience);
    }

    @Override
    public void insertBatchTagAudience(Audience audience) {
        audienceRepo.insertBatchToTagAudience(audience);
    }

    @Override
    public void deleteAudience(Long id) {
        audienceRepo.deleteAudience(id);
    }
}
