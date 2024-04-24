package com.example.personal_project.service;

import com.example.personal_project.model.Campaign;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface CampaignService {
    Campaign findCampaignById(Long id);
    List<Campaign> getAllCampaignsByAccount(String account);
    List<Campaign> getAllCompletedCampaigns();
    void updateCampaignExecuteStatus(Campaign campaign);
    Campaign insertNewCampaign(Campaign campaign, String status);
    void deleteCampaign(Long id);
    void sendCampaign(Campaign campaign) throws MessagingException, UnsupportedEncodingException;
    void sendCampaignById(Long id) throws MessagingException, UnsupportedEncodingException;
}
