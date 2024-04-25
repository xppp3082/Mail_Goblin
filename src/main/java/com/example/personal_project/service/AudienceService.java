package com.example.personal_project.service;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;

import java.util.List;
import java.util.Map;

public interface AudienceService {
    List<Audience> getAllAudienceByAccount(String account);

    List<Audience> getPageAudienceWithTagsByAccount(String account, int paging);

    List<Audience> getAudiencesWithTagsByCompanyId(Long companyId);

    List<Audience> searchAudiencesWithTagsByCompanyIdANDMail(Long companyId, String keyword);

    void updateMailCount(String audienceUUID, String account);

    void updateMailOpen(String audienceUUID, String companyUUID);

    void updateMailOpen(String audienceUUID);

    void updateMailClick(String audienceUUID, String companyUUID);

    void updateMailClick(String audienceUUID);

    void updateMailCount(String audienceUUID);

    Audience findAudienceByEmail(String email);

    List<Audience> getAllAudienceByCampaign(Campaign campaign);

    List<Audience> getAudienceByTag(Long tagId, String acount);

    Audience insertNewAudience(Audience audience);

    Map<String, Integer> getNewAudienceCountLastWeek(String account, Integer daysCount);

    void insertBatchTagAudience(Audience audience);

    void deleteAudience(Long id);

    Audience updateAudience(Audience audience);
}
