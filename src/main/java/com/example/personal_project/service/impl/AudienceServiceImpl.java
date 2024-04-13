package com.example.personal_project.service.impl;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.repository.AudienceRepo;
import com.example.personal_project.service.AudienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AudienceServiceImpl implements AudienceService {
    public final AudienceRepo audienceRepo;

    public AudienceServiceImpl(AudienceRepo audienceRepo) {
        this.audienceRepo = audienceRepo;
    }

    @Override
    public List<Audience> getAllAudience(String account) {
        return audienceRepo.retrieveAudienceByCompany(account);
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
}
