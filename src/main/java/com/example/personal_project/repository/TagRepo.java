package com.example.personal_project.repository;

import com.example.personal_project.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Slf4j
@Repository
public class TagRepo {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void insertTag(Tag tag){
        String sql = """
                INSERT INTO tag (name,company_id) VALUES (?,?); 
                """;
        try{
            jdbcTemplate.update(sql,tag.getName(),tag.getCompanyId());
        }catch (Exception e){
            log.error("insert tag under a company failed :" + e.getMessage());
        }
    }

}
