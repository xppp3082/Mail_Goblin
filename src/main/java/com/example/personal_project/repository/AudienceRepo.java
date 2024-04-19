package com.example.personal_project.repository;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@Transactional
public class AudienceRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;
    public Audience insertNewAudience(Audience audience) {
        String sql = """
            INSERT INTO audience 
            (name,email,birthday,audience_uuid,create_time,company_id,mailcount,opencount,clickcount) 
            VALUES
            (?,?,?,?,?,?,0,0,0)
            """;
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, audience.getName());
                ps.setString(2, audience.getEmail());
                ps.setDate(3, Date.valueOf(audience.getBirthday()));
                ps.setString(4, audience.getAudienceUUID());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(6, audience.getCompanyId());
                return ps;
            }, keyHolder);

            // 拿到生成的 ID
            Long generatedId = keyHolder.getKey().longValue();
            // 在此處進行其他操作，如果需要的話
            audience.setId(generatedId);
            return audience;
        } catch (Exception e) {
            log.error("error on creating new user in Repo layer : " + e.getMessage());
            return null;
        }
    }

    public void insertBatchToTagAudience(Audience audience){
        String sql = """
                INSERT INTO 
                tag_audience (tag_id,audience_id) 
                VALUES 
                (?,?)
                """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Tag tag = audience.getTagList().get(i);
                ps.setLong(1,tag.getId());
                ps.setLong(2,audience.getId());
            }
            @Override
            public int getBatchSize() {
                return audience.getTagList().size();
            }
        });
    }

    public void deleteAudience(Audience audience) {
        String sql = """
                DELETE FROM audience WHERE id = ?;
                """;
        try {
            jdbcTemplate.update(sql,audience.getId());
        } catch (Exception e) {
            log.error("error on deleting user in Repo layer : "+e.getMessage());
        }
    }

    public List<Audience> retrieveAudienceByCompany(String account) {
        String getAudienceQuery = """
                select * from audience where(select id from company where account =?);
                """;
        return jdbcTemplate.query(getAudienceQuery, (rs, rowNum) -> {
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
    public void updateUserMailCount(String audienceUUID, String account) {
        String updateQuery = """
                update audience set mailcount = mailcount +1 
                where audience_uuid = ? 
                and (select id from company where account = ?);
                """;
        try {
            jdbcTemplate.update(updateQuery, audienceUUID, account);
        } catch (Exception e) {
            log.error("update user mail count failed : " + e.getMessage());
        }
    }

    public void updateUserMailOpen(String audienceUUID, String companyUUID) {
        String updateQuery = """
                update audience 
                set opencount = opencount +1 
                where audience_uuid = ? 
                and exists(select id from company where company_uuid =?);
                """;
        try {
            jdbcTemplate.update(updateQuery, audienceUUID, companyUUID);
        } catch (Exception e) {
            log.error("update user open count failed : " + e.getMessage());
        }
    }

    public void updateUserMailOpen(String audienceUUID) {
        String updateQuery = """
                update audience 
                set opencount = opencount +1 
                where audience_uuid = ? ;
                """;
        try {
            jdbcTemplate.update(updateQuery, audienceUUID);
        } catch (Exception e) {
            log.error("update user open count failed : " + e.getMessage());
        }
    }

    public void updateUserMailClick(String audienceUUID, String companyUUID) {
        String updateQuery = """
                update audience set clickcount = clickcount +1 
                where audience_uuid = ? 
                and (select id from company where company_uuid =?);
                """;
        try {
            jdbcTemplate.update(updateQuery, audienceUUID, companyUUID);
        } catch (Exception e) {
            log.error("update user click count failed : " + e.getMessage());
        }
    }


    public void updateUserMailClick(String audienceUUID) {
        String updateQuery = """
                update audience set clickcount = clickcount +1 
                where audience_uuid = ? ;
                """;
        try {
            jdbcTemplate.update(updateQuery, audienceUUID);
        } catch (Exception e) {
            log.error("update user click count failed : " + e.getMessage());
        }
    }

    public void updateUserMailCount(String audienceUUID) {
        String updateQuery = """
                update audience set mailcount = mailcount +1 
                where audience_uuid = ? ;
                """;
        try {
            jdbcTemplate.update(updateQuery, audienceUUID);
        } catch (Exception e) {
            log.error("update user mail count failed : " + e.getMessage());
        }
    }

    public List<Audience> getAllAudienceByCampaign(Campaign campaign) {
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
        RowMapper<Audience> mapper = getAudienceRowMapper();
        List<Audience> audiences = jdbcTemplate.query(sql, mapper, campaign.getId());
        return audiences;
    }

    public Audience finAudienceByEmail(String email) {
        String sql = "SELECT * FROM audience WHERE email = ?";
//        String sql = "SELECT id, name, email, birthday, audience_uuid, mailcount, opencount, clickcount, create_time FROM audience WHERE email = ?";
        RowMapper<Audience> mapper = orginAudienceRowMapper();
        return jdbcTemplate.queryForObject(sql, mapper, email);
    }

    public RowMapper<Audience> orginAudienceRowMapper() {
        return new RowMapper<Audience>() {
            @Override
            public Audience mapRow(ResultSet rs, int rowNum) throws SQLException {
                Audience audience = new Audience();
                audience.setId(rs.getLong("id"));
                audience.setName(rs.getString("name"));
                audience.setEmail(rs.getString("email"));
                audience.setBirthday(rs.getString("birthday"));
                audience.setAudienceUUID(rs.getString("audience_uuid"));
                audience.setMailCount(rs.getInt("mailcount"));
                audience.setOpenCount(rs.getInt("opencount"));
                audience.setClickCount(rs.getInt("clickcount"));
                audience.setCreateTime(rs.getTimestamp("create_time"));
                return audience;
            }
        };
    }

    public RowMapper<Audience> getAudienceRowMapper() {
        return new RowMapper<Audience>() {
            @Override
            public Audience mapRow(ResultSet rs, int rowNum) throws SQLException {
                Audience audience = new Audience();
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
