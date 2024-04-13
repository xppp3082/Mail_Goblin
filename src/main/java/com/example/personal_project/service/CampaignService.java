package com.example.personal_project.service;

import com.example.personal_project.model.Campaign;

import java.util.List;

public interface CampaignService {
    List<Campaign> getAllCampaigns();
    List<Campaign> getAllCompletedCampaigns();
    void updateCampaignExecuteStatus(Campaign campaign);
}
