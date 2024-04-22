package com.example.personal_project.repository;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.status.CampaignStatus;
import com.example.personal_project.model.status.ExecuteStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
@Slf4j
@Repository
@Transactional
public class CampaignRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Campaign> getAllCampaigns(){
        String sql = """
                select * from campaign;
                """;
        RowMapper<Campaign> mapper = originCampaignRowMapper();
        List<Campaign>campaigns = jdbcTemplate.query(sql,mapper);
        return campaigns;
    }

    public List<Campaign> getAllCampaignsByAccount(String account){
        String sql = """
                select campaign.* from campaign
                left join template 
                on 
                campaign.template_id= template.id
                where 
                template.company_id = (select id from company where account = ?);
                """;
        RowMapper<Campaign>mapper = originCampaignRowMapper();
        List<Campaign> campaigns = jdbcTemplate.query(sql,mapper,account);
        return campaigns;
    }

    public List<Campaign> getAllCompletedCampaigns(){
        String sql = """
                select * from campaign where status =?;
                """;
        RowMapper<Campaign> mapper = originCampaignRowMapper();
        List<Campaign>campaigns = jdbcTemplate.query(sql,mapper, CampaignStatus.COMPLETED.name());
        return campaigns;
    }

    public void updateCampaignExecuteStatus(Campaign campaign){
        String sql = """
                update campaign set execute_status = ? where id = ?
                """;
        try{
            jdbcTemplate.update(sql, ExecuteStatus.COMPLETE.name(),campaign.getId());
        }catch (Exception e){
            log.error("update campaign execute_status failed :" + e.getMessage());
        }
    }

    public Campaign insertNewCampaign(Campaign campaign){
        String sql = """
                INSERT INTO campaign 
                (template_id,subject,target_date,status,tag_id,execute_status)
                VALUES
                (?,?,?,?,?,?);
                """;
        try{
            jdbcTemplate.update(sql,
                    campaign.getTemplateId(),campaign.getSubject(),
                    campaign.getSendDate(),campaign.getStatus(),
                    campaign.getTagId(),campaign.getExecuteStatus());
        }catch (Exception e){
            log.error("Error on inserting new campaign with templateId : " +campaign.getTemplateId());
        }
        return campaign;
    }

    public RowMapper<Campaign> originCampaignRowMapper(){
        return new RowMapper<Campaign>() {
            @Override
            public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
                Campaign campaign = new Campaign();
                campaign.setId(rs.getLong("id"));
                campaign.setTemplateId(rs.getLong("template_id"));
                campaign.setSubject(rs.getString("subject"));
                Date senDateSQL = rs.getDate("target_date");
                LocalDate sendDate = senDateSQL.toLocalDate();
                campaign.setSendDate(sendDate);
                campaign.setStatus(rs.getString("status"));
                campaign.setTagId(rs.getLong("tag_id"));
                campaign.setAutomationId(rs.getLong("automation_id"));
                campaign.setExecuteStatus(rs.getString("execute_status"));
                return campaign;
            }
        };
    }
}
