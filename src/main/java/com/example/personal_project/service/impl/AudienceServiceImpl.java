package com.example.personal_project.service.impl;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.repository.AudienceRepo;
import com.example.personal_project.service.AudienceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    @Override
    public Audience insertNewAudience(Audience audience){
        UUID uuid = UUID.randomUUID();
        audience.setAudienceUUID(uuid.toString());
        Audience newAudience =audienceRepo.insertNewAudience(audience);
        return newAudience;
    }

    @Override
    public void insertBatchTagAudience(Audience audience) {
        audienceRepo.insertBatchToTagAudience(audience);
    }

    @Override
    public void deleteAudience(Audience audience) {
        audienceRepo.deleteAudience(audience);
    }
}