package com.example.personal_project.repository;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.status.CampaignStatus;
import com.example.personal_project.model.status.ExecuteStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.SpELContext;
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
        RowMapper<Campaign> mapper = orginCampaignRowMapper();
        List<Campaign>campaigns = jdbcTemplate.query(sql,mapper);
        return campaigns;
    }

    public List<Campaign> getAllCompletedCampaigns(){
        String sql = """
                select * from campaign where status =?;
                """;
        RowMapper<Campaign> mapper = orginCampaignRowMapper();
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

    public RowMapper<Campaign>orginCampaignRowMapper(){
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
                campaign.setTagId(rs.getLong("tag_id"));
                campaign.setAutomationId(rs.getLong("automation_id"));
                campaign.setExecuteStatus(rs.getString("execute_status"));
                return campaign;
            }
        };
    }
}
