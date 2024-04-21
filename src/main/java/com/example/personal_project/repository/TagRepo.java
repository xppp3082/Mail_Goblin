package com.example.personal_project.repository;

import com.example.personal_project.model.Tag;
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

    public  void deleteTag(Long tagId,Long companyId){
        String sql = """
                DELETE FROM tag WHERE tag.id = ? and tag.company_id= ?
                """;
        try {
            jdbcTemplate.update(sql,tagId,companyId);
        }catch (Exception e){
            log.error("delete tag under a company failed :" + e.getMessage());
        }
    }

    public List<Tag> getAllTagsByCompanyId(Long companyId){
        String sql = """
                SELECT * FROM tag WHERE company_id = ?
                """;
        RowMapper<Tag> mapper = originTagRowMapper();
        List<Tag> tags = jdbcTemplate.query(sql,mapper,companyId);
        return tags;
    }

    public List<Tag> getAllTagsByCompanyAccount(String account){
        String sql = """
                SELECT tag.* FROM tag
                LEFT JOIN company
                ON tag.company_id =company.id
                WHERE company.account = ?;
                """;
        RowMapper<Tag> mapper = originTagRowMapper();
        List<Tag> tags = jdbcTemplate.query(sql,mapper,account);
        return tags;
    }

    public RowMapper<Tag> originTagRowMapper(){
        return new RowMapper<Tag>() {
            @Override
            public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
                Tag tag = new Tag();
                tag.setId(rs.getLong("id"));
                tag.setName(rs.getString("name"));
                tag.setCompanyId(rs.getLong("company_id"));
                return tag;
            }
        };
    }

}
