package com.example.personal_project.repository;

import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.MailTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
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

//    public void insertMailTemplateWithAcount(String account,MailTemplate mailTemplate) {
//        String sql = """
//                INSERT INTO template
//                (company_id,url,subject,content,picture)
//                VALUES
//                ((SELECT id FROM company WHERE account =?),?,?,?,?);
//                """;
//        try {
//            jdbcTemplate.update(sql,
//                    account, mailTemplate.getUrl(),
//                    mailTemplate.getSubject(), mailTemplate.getContent(),
//                    mailTemplate.getPicture());
//        } catch (Exception e) {
//            log.error("error on insert new mailTemplate : " + e.getMessage());
//        }
//    }

    public MailTemplate insertMailTemplateWithAcount(String account,MailTemplate mailTemplate) {
        String sql = """
                INSERT INTO template 
                (company_id,url,subject,content,picture) 
                VALUES 
                ((SELECT id FROM company WHERE account =?),?,?,?,?);
                """;
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            PreparedStatement ps  = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                            ps.setString(1,account);
                            ps.setString(2,mailTemplate.getUrl());
                            ps.setString(3,mailTemplate.getSubject());
                            ps.setString(4,mailTemplate.getContent());
                            ps.setString(5,mailTemplate.getPicture());
                            return ps;
                        }
                    },keyHolder);
            Long generateId = keyHolder.getKey().longValue();
            mailTemplate.setId(generateId);
            return mailTemplate;
        } catch (Exception e) {
            log.error("error on insert new mailTemplate : " + e.getMessage());
            return null;
        }
    }

    public void updateMailTemplate(MailTemplate mailTemplate){
        String sql = """
                UPDATE template 
                SET url = ?,subject = ?,content = ?, picture = ? 
                WHERE id = ?;
                """;
        try{
            jdbcTemplate.update(sql,
                    mailTemplate.getUrl(),
                    mailTemplate.getSubject(), mailTemplate.getContent(),
                    mailTemplate.getPicture(), mailTemplate.getId());
        }catch (Exception e){
            log.error("Error updating mail template with ID " + mailTemplate.getId() + ": " + e.getMessage());
        }
    }

    public void deleteMailTemplate(Long id){
        String sql = """
                DELETE FROM template WHERE id = ?;
                """;
        try{
            jdbcTemplate.update(sql,id);
        }catch (Exception e){
            log.error("Error deleting mail template with id " + id +" : " + e.getMessage());
        }
    }

    public  MailTemplate findMailTemplateById(Long templateId){
        String sql = """
                SELECT * FROM template WHERE id = ?;
                """;
        try{
            return jdbcTemplate.queryForObject(sql,new Object[]{templateId},originTemplateRowMapper());
        }catch (EmptyResultDataAccessException e){
            log.error("No mail template found with ID: " + templateId);
            return null;
        }catch (Exception e){
            log.error("Error on retrieving mail template with ID " + templateId + ": " + e.getMessage());
            return null;
        }
    }
    public List<MailTemplate> getAllMailTemplateBByCompany(Long company_id){
        String sql = """
                SELECT * FROM template WHERE company_id = ?;
                """;
        try {
            RowMapper<MailTemplate> mapper = originTemplateRowMapper();
            List<MailTemplate> templates = jdbcTemplate.query(sql,mapper,company_id);
            return templates;
        }catch (Exception e){
            log.error("error on catching all the template under the company with id : "+company_id);
        }
        return  null;
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
