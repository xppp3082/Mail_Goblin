package com.example.personal_project.repository;

import com.example.personal_project.model.Audience;
import com.example.personal_project.model.Campaign;
import com.example.personal_project.model.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
        } catch (DuplicateKeyException e) {
            //company_id和email為複合唯一鍵
            log.error("Duplicate account error on creating new audience under this company: " + e.getMessage());
            throw new DuplicateKeyException("Audience already exists.");
        } catch (Exception e) {
            log.error("error on creating new user in Repo layer : " + e.getMessage());
            return null;
        }
    }

    public Audience updateAudience(Audience audience) {
        String sql = """
                UPDATE audience SET
                name = ?,
                email = ?,
                birthday= ? where id =? and company_id = ?;
                """;
        try {
            int rowsUpdated = jdbcTemplate.update(sql,
                    audience.getName(), audience.getEmail(),
                    audience.getBirthday(), audience.getId(), audience.getCompanyId());
            if (rowsUpdated > 0) {
                return audience; // 返回更新后的 Audience 对象
            } else {
                return null; // 或者根据业务需求返回其他值，比如 throw 一个异常
            }
        } catch (Exception e) {
            log.error("Error updating audience with ID " + audience.getId() + ": " + e.getMessage());
            return null; // 或者根据业务需求返回其他值，比如 throw 一个异常
        }
    }

    public void insertBatchToTagAudience(Audience audience) {
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
                ps.setLong(1, tag.getId());
                ps.setLong(2, audience.getId());
            }

            @Override
            public int getBatchSize() {
                return audience.getTagList().size();
            }
        });
    }

    public void deleteAudience(Long id) {
        String sql = """
                DELETE FROM audience WHERE id = ?;
                """;
        try {
            jdbcTemplate.update(sql, id);
        } catch (Exception e) {
            log.error("error on deleting user in Repo layer : " + e.getMessage());
        }
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

    public List<Audience> getAllAudienceByAccount(String account) {
        String sql = """
                select au.* from audience
                 as au 
                 left join company 
                 as co 
                 on au.company_id = co.id 
                 where co.account = ?;
                """;
        RowMapper<Audience> mapper = orginAudienceRowMapper();
        List<Audience> audiences = jdbcTemplate.query(sql, mapper, account);
        return audiences;
    }

    public List<Audience> getPageAudienceWithTagsByAccount(String account, int pageSize, int offset) {
        String sql = """
                SELECT au.*, tag.id as ta_id, tag.company_id as ta_cid, tag.name AS tagName
                FROM (
                    SELECT *
                    FROM audience
                    WHERE company_id = (SELECT id FROM company WHERE account = ?) 
                    LIMIT ? OFFSET ?
                ) AS au
                LEFT JOIN tag_audience AS tg ON tg.audience_id = au.id
                LEFT JOIN tag ON tag.id = tg.tag_id
                ORDER BY id DESC;
                """;
        Map<Long, Audience> audienceMap = new LinkedHashMap<>();
        jdbcTemplate.query(sql, new Object[]{account, pageSize, offset}, rs -> {
            Long audienceId = rs.getLong("id");
            Audience audience;
            if (!audienceMap.containsKey(audienceId)) {
                audience = new Audience();
                audience.setId(audienceId);
                audience.setName(rs.getString("name"));
                audience.setEmail(rs.getString("email"));
                audience.setBirthday(rs.getString("birthday"));
                audience.setCompanyId(rs.getLong("company_id"));
                audience.setMailCount(rs.getInt("mailcount"));
                audience.setOpenCount(rs.getInt("opencount"));
                audience.setClickCount(rs.getInt("clickcount"));
                audienceMap.put(audienceId, audience);
            } else {
                audience = audienceMap.get(audienceId);
            }
            String tagName = rs.getString("tagName");
            Long tagId = rs.getLong("ta_id");
            Long tag_CompanyId = rs.getLong("ta_cid");
            if (tagName != null) {
                Tag tag = new Tag();
                tag.setId(tagId);
                tag.setCompanyId(tag_CompanyId);
                tag.setName(tagName);

                if (audience.getTagList() == null) {
                    audience.setTagList(new ArrayList<>());
                }
                audience.getTagList().add(tag);
            }
        });
        return new ArrayList<>(audienceMap.values());
    }

    public List<Audience> getAudiencesWithTagsByCompanyId(Long companyId) {
        String sql = """
                SELECT au.*,tag.id as ta_id,tag.company_id as ta_cid, tag.name AS tagName
                 FROM audience AS au
                 LEFT JOIN tag_audience AS tg ON tg.audience_id = au.id
                 LEFT JOIN tag ON tag.id = tg.tag_id
                 WHERE au.company_id = ?
                 """;
        Map<Long, Audience> audienceMap = new HashMap<>();
        jdbcTemplate.query(sql, new Object[]{companyId}, rs -> {
            Long audienceId = rs.getLong("id");
            Audience audience;
            if (!audienceMap.containsKey(audienceId)) {
                audience = new Audience();
                audience.setId(audienceId);
                audience.setName(rs.getString("name"));
                audience.setEmail(rs.getString("email"));
                audience.setBirthday(rs.getString("birthday"));
                audience.setCompanyId(rs.getLong("company_id"));
                audience.setMailCount(rs.getInt("mailcount"));
                audience.setOpenCount(rs.getInt("opencount"));
                audience.setClickCount(rs.getInt("clickcount"));
                audienceMap.put(audienceId, audience);
            } else {
                audience = audienceMap.get(audienceId);
            }

            String tagName = rs.getString("tagName");
            Long tagId = rs.getLong("ta_id");
            Long tag_CompanyId = rs.getLong("ta_cid");
            if (tagName != null) {
                Tag tag = new Tag();
                tag.setId(tagId);
                tag.setCompanyId(tag_CompanyId);
                tag.setName(tagName);

                if (audience.getTagList() == null) {
                    audience.setTagList(new ArrayList<>());
                }
                audience.getTagList().add(tag);
            }
        });
        return new ArrayList<>(audienceMap.values());
    }

    public List<Audience> searchAudiencesWithTagsByCompanyIdANDMail(Long companyId, String keyword) {
        String sql = """
                SELECT au.*,tag.id as ta_id,tag.company_id as ta_cid, tag.name AS tagName
                 FROM audience AS au
                 LEFT JOIN tag_audience AS tg ON tg.audience_id = au.id
                 LEFT JOIN tag ON tag.id = tg.tag_id
                 WHERE au.company_id = ? and au.email like ?
                 """;
        Map<Long, Audience> audienceMap = new HashMap<>();
        keyword = MessageFormat.format("%{0}%", keyword);
        jdbcTemplate.query(sql, new Object[]{companyId, keyword}, rs -> {
            Long audienceId = rs.getLong("id");
            Audience audience;
            if (!audienceMap.containsKey(audienceId)) {
                audience = new Audience();
                audience.setId(audienceId);
                audience.setName(rs.getString("name"));
                audience.setEmail(rs.getString("email"));
                audience.setBirthday(rs.getString("birthday"));
                audience.setCompanyId(rs.getLong("company_id"));
                audienceMap.put(audienceId, audience);
            } else {
                audience = audienceMap.get(audienceId);
            }

            String tagName = rs.getString("tagName");
            Long tagId = rs.getLong("ta_id");
            Long tag_CompanyId = rs.getLong("ta_cid");
            if (tagName != null) {
                Tag tag = new Tag();
                tag.setId(tagId);
                tag.setCompanyId(tag_CompanyId);
                tag.setName(tagName);

                if (audience.getTagList() == null) {
                    audience.setTagList(new ArrayList<>());
                }
                audience.getTagList().add(tag);
            }
        });
        return new ArrayList<>(audienceMap.values());
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

    public List<Audience> getAllAudienceByTagId(Long tagId, String account) {
        String sql = """
                SELECT au.* FROM audience AS au
                LEFT JOIN tag_audience AS tau ON tau.audience_id=au.id
                LEFT JOIN tag on tau.audience_id=tag.id
                LEFT JOIN company AS com ON au.company_id=com.id
                WHERE tau.tag_id=? AND com.account = ?;
                """;
        RowMapper<Audience> mapper = orginAudienceRowMapper();
        List<Audience> audiences = jdbcTemplate.query(sql, mapper, tagId, account);
        return audiences;
    }

    public Map<String, Integer> getNewAudienceCountLastWeek(String account, Integer daysCount) {
        Map<String, Integer> newAudienceCountMap = new LinkedHashMap<>();
        for (int i = 0; i < daysCount; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateString = date.toString();
            int newAUdienceCount = getNewAudienceCountForDate(date, account);
            newAudienceCountMap.put(dateString, newAUdienceCount);
        }
        return newAudienceCountMap;
    }

    private int getNewAudienceCountForDate(LocalDate date, String account) {
        String sql = """
                SELECT COUNT(*) FROM audience 
                LEFT JOIN company 
                ON audience.company_id = company.id
                WHERE DATE(create_time) = ?
                AND
                company.account = ?
                """;
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, date, account);
        } catch (Exception e) {
            log.error("error on getting new added audience for past days : " + e.getMessage());
            return 0;
        }
    }

    public Audience finAudienceByEmail(String email) {
        String sql = "SELECT * FROM audience WHERE email = ?";
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
