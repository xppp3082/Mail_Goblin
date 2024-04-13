package com.example.personal_project.service;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;

import java.util.List;

public interface AudienceService {
    List<Audience> getAllAudience(String account);
    void updateMailCount(String audienceUUID, String account);
    void updateMailOpen(String audienceUUID,String companyUUID);
    void updateMailOpen(String audienceUUID);
    void updateMailClick(String audienceUUID,String companyUUID);
    void updateMailClick(String audienceUUID);
    void updateMailCount(String audienceUUID);
    Audience findAudienceByEmail(String email);
    List<Audience>getAllAudienceByCampaign(Campaign campaign);
}
