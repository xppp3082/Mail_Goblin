package com.example.personal_project.repository;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
public class MailTemplateRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertMailTemplate(MailTemplate mailTemplate) {
        String sql = """
                INSERT INTO template 
                (company_id,url,subject,content,picture) 
                VALUES 
                (?,?,?,?,?);
                """;
        try {
            jdbcTemplate.update(sql,
                    mailTemplate.getCompanyId(), mailTemplate.getUrl(),
                    mailTemplate.getSubject(), mailTemplate.getContent(),
                    mailTemplate.getPicture());
        } catch (Exception e) {
            log.error("error on insert new mailTemplate : " + e.getMessage());
        }
    }

    public MailTemplate getMailTemplateByCampaign(Campaign campaign) {
        String sql = """
                select 
                mt.id,mt.company_id,mt.url,mt.subject,mt.content,mt.picture 
                from template as mt 
                left join 
                campaign as ca 
                on mt.id = ca.template_id 
                where ca.id = ?;
                """;
        MailTemplate targetTemplate = null;
        try {
            RowMapper<MailTemplate> mapper = originTemplateRowMapper();
            List<MailTemplate> templates = jdbcTemplate.query(sql, mapper, campaign.getId());
            targetTemplate = templates.get(0);
        } catch (Exception e) {
            log.error("error on finding mail template through campaign : " + e.getMessage());
        }
        return targetTemplate;
    }

    public RowMapper<MailTemplate> originTemplateRowMapper() {
        return new RowMapper<MailTemplate>() {
            @Override
            public MailTemplate mapRow(ResultSet rs, int rowNum) throws SQLException {
                MailTemplate mailTemplate = new MailTemplate();
                mailTemplate.setId(rs.getLong("id"));
                mailTemplate.setCompanyId(rs.getLong("company_id"));
                mailTemplate.setUrl(rs.getString("url"));
                mailTemplate.setSubject(rs.getString("subject"));
                mailTemplate.setContent(rs.getString("content"));
                mailTemplate.setPicture(rs.getString("picture"));
                return mailTemplate;
            }
        };
    }
}
