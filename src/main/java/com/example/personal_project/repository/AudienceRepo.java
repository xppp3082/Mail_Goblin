package com.example.personal_project.repository;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@Transactional
public class AudienceRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Audience> retrieveAudienceByCompany(String account){
        String getAudienceQuery = """
                select * from audience where(select id from company where account =?);
                """;
        return  jdbcTemplate.query(getAudienceQuery,(rs, rowNum) -> {
            Audience audience = new Audience();
            audience.setName(rs.getString("name"));
            audience.setEmail(rs.getString("email"));
            audience.setEmail(rs.getString("birthday"));
            audience.setAudienceUUID(rs.getString("audience_uuid"));
            audience.setMailCount(rs.getInt("mailcount"));
            audience.setOpenCount(rs.getInt("opencount"));
            audience.setClickCount(rs.getInt("clickcount"));
            audience.setCreateTime(rs.getTimestamp("timestamp"));
            audience.setCompanyId(rs.getLong("company_id"));
            return audience;
        });
    }
    //根據user的UUID與company account將mailcount+=1
    public void updateUserMailCount(String audienceUUID, String account){
        String updateQuery = """
                update audience set mailcount = mailcount +1 
                where audience_uuid = ? 
                and (select id from company where account = ?);
                """;
        try{
            jdbcTemplate.update(updateQuery,audienceUUID,account);
        }catch (Exception e){
            log.error("update user mail count failed : " +e.getMessage());
        }
    }

    public void updateUserMailOpen(String audienceUUID,String companyUUID){
        String updateQuery = """
                update audience 
                set opencount = opencount +1 
                where audience_uuid = ? 
                and exists(select id from company where company_uuid =?);
                """;
        try {
            jdbcTemplate.update(updateQuery,audienceUUID,companyUUID);
        }catch (Exception e){
            log.error("update user open count failed : "+e.getMessage());
        }
    }

    public void updateUserMailOpen(String audienceUUID){
        String updateQuery = """
                update audience 
                set opencount = opencount +1 
                where audience_uuid = ? ;
                """;
        try {
            jdbcTemplate.update(updateQuery,audienceUUID);
        }catch (Exception e){
            log.error("update user open count failed : "+e.getMessage());
        }
    }

    public void updateUserMailClick(String audienceUUID,String companyUUID){
        String updateQuery = """
                update audience set clickcount = clickcount +1 
                where audience_uuid = ? 
                and (select id from company where company_uuid =?);
                """;
        try {
            jdbcTemplate.update(updateQuery,audienceUUID,companyUUID);
        }catch (Exception e){
            log.error("update user click count failed : "+e.getMessage());
        }
    }

    public void updateUserMailClick(String audienceUUID){
        String updateQuery = """
                update audience set clickcount = clickcount +1 
                where audience_uuid = ? ;
                """;
        try {
            jdbcTemplate.update(updateQuery,audienceUUID);
        }catch (Exception e){
            log.error("update user click count failed : "+e.getMessage());
        }
    }

    public List<Audience> getAllAudienceByCampaign(Campaign campaign){
        String sql = """
                select a.*,t.name as tag  from audience as a 
                left join 
                tag_audience as ta on a.id = ta.audience_id 
                left join 
                tag as t on ta.tag_id = t.id 
                left join 
                campaign as ca on t.id = ca.tag_id 
                where ca.id = ?;
                """;
        RowMapper<Audience>mapper = getAudienceRowMapper();
        List<Audience> audiences = jdbcTemplate.query(sql,mapper,campaign.getId());
        return audiences;
    }


    public RowMapper<Audience> getAudienceRowMapper(){
        return new RowMapper<Audience>() {
            @Override
            public Audience mapRow(ResultSet rs, int rowNum) throws SQLException {
                Audience audience =new Audience();
                audience.setId(rs.getLong("id"));
                audience.setName(rs.getString("name"));
                audience.setEmail(rs.getString("email"));
                audience.setBirthday(rs.getString("birthday"));
                audience.setAudienceUUID(rs.getString("audience_uuid"));
                audience.setMailCount(rs.getInt("mailcount"));
                audience.setOpenCount(rs.getInt("opencount"));
                audience.setClickCount(rs.getInt("clickcount"));
                audience.setCreateTime(rs.getTimestamp("create_time"));
                audience.setTagName(rs.getString("tag"));
                return audience;
            }
        };
    }

}
