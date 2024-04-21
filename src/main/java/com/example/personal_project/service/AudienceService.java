package com.example.personal_project.service;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;

import java.util.List;

public interface AudienceService {
    List<Audience> getAllAudienceByAccount(String account);
    List<Audience>getAudiencesWithTagsByCompanyId(Long companyId);
    List<Audience>searchAudiencesWithTagsByCompanyIdANDMail(Long companyId,String keyword);
    void updateMailCount(String audienceUUID, String account);
    void updateMailOpen(String audienceUUID,String companyUUID);
    void updateMailOpen(String audienceUUID);
    void updateMailClick(String audienceUUID,String companyUUID);
    void updateMailClick(String audienceUUID);
    void updateMailCount(String audienceUUID);
    Audience findAudienceByEmail(String email);
    List<Audience>getAllAudienceByCampaign(Campaign campaign);
    Audience insertNewAudience(Audience audience);
    void insertBatchTagAudience(Audience audience);
    void deleteAudience(Long id);
    Audience updateAudience(Audience audience);
}
