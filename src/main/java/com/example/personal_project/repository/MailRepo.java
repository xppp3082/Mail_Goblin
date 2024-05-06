package com.example.personal_project.repository;

import com.example.personal_project.model.Mail;
import com.example.personal_project.model.MailHook;
import com.example.personal_project.model.status.DeliveryStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class MailRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertBatch(List<Mail> mails) {
        String sql = """
                INSERT INTO 
                mail (campaign_id,recipient_mail,subject,status,send_date,timestamp,checktimes,audience_id) 
                VALUES 
                (?,?,?,?,?,?,?,?);
                """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Mail mail = mails.get(i);
                //Set values for prepareStatement
                ps.setLong(1, mail.getCampaignID());
                ps.setString(2, mail.getRecipientMail());
                ps.setString(3, mail.getSubject());
                ps.setString(4, mail.getStatus());
                ps.setString(5, mail.getSendDate().toString());
                ps.setString(6, mail.getTimestamp().toString());
                ps.setInt(7, mail.getCheckTimes());
                ps.setLong(8, mail.getAudienceID());
            }

            @Override
            public int getBatchSize() {
                return mails.size();
            }
        });
    }

    public void updateBatchByMimeId(List<Mail> mails) {
        String sql = """
                UPDATE mail 
                SET 
                    campaign_id = ?,
                    send_date = ?,
                    timestamp = ?,
                    checktimes = ?,
                    audience_id = ?
                WHERE 
                    mime_id = ?;
                """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Mail mail = mails.get(i);
                // Set values for PreparedStatement
                ps.setLong(1, mail.getCampaignID());
                ps.setString(2, mail.getSendDate().toString());
                ps.setString(3, mail.getTimestamp().toString());
                ps.setInt(4, mail.getCheckTimes());
                ps.setLong(5, mail.getAudienceID());
                ps.setString(6, mail.getMimeID());
            }

            @Override
            public int getBatchSize() {
                return mails.size();
            }
        });
    }

    public void insertReiveRecordWithMailHook(MailHook mailHook) {
        String sql = """
                INSERT INTO
                mail (recipient_mail,subject,status,mime_id)
                VALUES(?,?,?,?);
                """;
        try {
            jdbcTemplate.update(sql,
                    mailHook.getRecipientMail(), mailHook.getSubject(),
                    mailHook.getDeliveryStatus(), mailHook.getMimeID());
            log.info("successfully insert delivery info by mailgun webhook :" + mailHook.getMimeID());
        } catch (Exception e) {
            log.error("error on insert delivery record by mailgun webhook : " + e.getMessage());
        }
    }

    public void insertEventRecord(String campaignId, String eventType, String audienceUUId, String recipientMail, String subject) {
        String sql = """
                INSERT INTO 
                mail (campaign_id,status,send_date,timestamp,audience_id,recipient_mail,subject)
                VALUES (?,?,?,?,(select id from audience where audience.audience_uuid =?),?,?);
                """;
        try {
            jdbcTemplate.update(sql,
                    campaignId, eventType,
                    LocalDate.now(), Timestamp.valueOf(LocalDateTime.now()), audienceUUId,
                    recipientMail, subject);
        } catch (Exception e) {
            log.error("error on insert open record with userId : " + audienceUUId + " : " + e.getMessage());
        }
    }

    public Double getMailDeliveryRateForCompany(String account) {
        LocalDate startDate = LocalDate.now().minusMonths(1);
        String sql = """
                SELECT COUNT(*) FROM mail as m 
                LEFT JOIN campaign AS c ON m.campaign_id = c.id
                LEFT JOIN template AS t ON c.template_id = t.id
                WHERE t.company_id = (SELECT id FROM company WHERE account = ? )
                AND m.status = ?
                AND m.send_date >= ?
                """;
        Integer successfulMailsCount = jdbcTemplate.queryForObject(sql,
                Integer.class, account, DeliveryStatus.RECEIVE.name(), startDate);

        //計算郵件總量
        sql = """
                SELECT COUNT(*) FROM mail AS m 
                LEFT JOIN campaign AS c ON m.campaign_id = c.id 
                LEFT JOIN template AS t ON c.template_id = t.id 
                WHERE t.company_id = (SELECT id FROM company WHERE account = ? )
                AND (m.status =? OR m.status = ?)
                AND m.send_date >= ?
                """;
        Integer totalMailCount = jdbcTemplate.queryForObject(sql,
                Integer.class, account, DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name(), startDate);

        //計算郵件傳送率
        if (totalMailCount != null && totalMailCount > 0) {
            return (double) successfulMailsCount / totalMailCount;
        } else {
            return 0.0;
        }
    }

    public Map<LocalDate, Double> getDailyMailDeliveryRate(Long companyID) {
        Map<LocalDate, Double> dailyDeliveryRates = new LinkedHashMap<>();

        //拿到過去30天的起始日期
        LocalDate startDate = LocalDate.now().minusDays(30);

        //循環計算每一天的郵件送達率
        for (LocalDate date = startDate; !date.isAfter(LocalDate.now()); date = date.plusDays(1)) {
            //計算當天郵件傳送成功的數量
            String sql = """
                    SELECT COUNT(*) FROM mail m
                    LEFT JOIN campaign c ON m.campaign_id =c.id
                    LEFT JOIN template t ON c.template_id = t.id
                    WHERE t.company_id = ?
                    AND DATE(m.send_date) = ?
                    AND m.status = ?
                    """;
            Integer successfulMailsCount = jdbcTemplate.queryForObject(sql,
                    Integer.class, companyID, date, DeliveryStatus.RECEIVE.name());

            sql = """
                    SELECT COUNT(*) FROM mail m 
                    LEFT JOIN campaign c ON m.campaign_id = c.id
                    LEFT JOIN template t ON c.template_id = t.id
                    WHERE t.company_id = ?
                    AND DATE (m.send_date) =?
                    AND (m.status = ? OR m.status = ?)
                    """;
            Integer totalMailsCount = jdbcTemplate.queryForObject(sql,
                    Integer.class, companyID, date, DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name());

            // 計算郵件送達率
            double deliveryRate = totalMailsCount != null && totalMailsCount > 0 ?
                    (double) successfulMailsCount / totalMailsCount : 0.0;

            // 將日期和對應的郵件送達率存儲在 Map 中
            dailyDeliveryRates.put(date, deliveryRate);
        }

        return dailyDeliveryRates;
    }

    public Map<LocalDate, Double> trackDailyMailDeliveryRate(String account) {
        Map<LocalDate, Double> dailyDeliveryRates = new LinkedHashMap<>();
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().plusDays(1);
        // 初始化所有日期的成功率為 0.0
        for (int i = 0; i <= 30; i++) {
            dailyDeliveryRates.put(startDate.plusDays(i), 0.0);
        }
        String sql = """
                SELECT DATE(m.send_date) AS send_date,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS successful_mails,
                SUM(CASE WHEN m.status = ? OR m.status = ? THEN 1 ELSE 0 END) AS total_mails
                FROM mail m
                LEFT JOIN campaign c ON m.campaign_id = c.id
                LEFT JOIN template t ON c.template_id = t.id
                WHERE t.company_id = (SELECT id FROM company WHERE account = ? )
                AND m.send_date BETWEEN ? AND ?
                GROUP BY DATE(m.send_date)
                """;
        // 執行 SQL 查詢
        jdbcTemplate.query(sql, rs -> {
                    LocalDate sendDate = rs.getDate("send_date").toLocalDate();
                    int successfulMailsCount = rs.getInt("successful_mails");
                    int totalMailsCount = rs.getInt("total_mails");

                    // 計算郵件送達率
                    double deliveryRate = totalMailsCount > 0 ?
                            (double) successfulMailsCount / totalMailsCount : 0.0;

                    // 將日期和對應的郵件送達率存儲在 Map 中
                    dailyDeliveryRates.put(sendDate, deliveryRate);
                }, DeliveryStatus.RECEIVE.name(), DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name(),
                account, startDate, endDate);

        return dailyDeliveryRates;
    }

    public Map<LocalDate, Double> trackDailyMailDeliveryRateByDate(String account, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Double> dailyDeliveryRates = new LinkedHashMap<>();
        // 初始化所有日期的成功率為 0.0
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dailyDeliveryRates.put(date, 0.0);
        }
        String sql = """
                SELECT DATE(m.send_date) AS send_date,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS successful_mails,
                SUM(CASE WHEN m.status = ? OR m.status = ? THEN 1 ELSE 0 END) AS total_mails
                FROM mail m
                LEFT JOIN campaign c ON m.campaign_id = c.id
                LEFT JOIN template t ON c.template_id = t.id
                WHERE t.company_id = (SELECT id FROM company WHERE account = ? )
                AND m.send_date BETWEEN ? AND ?
                GROUP BY DATE(m.send_date)
                """;
        // 執行 SQL 查詢
        jdbcTemplate.query(sql, rs -> {
                    LocalDate sendDate = rs.getDate("send_date").toLocalDate();
                    int successfulMailsCount = rs.getInt("successful_mails");
                    int totalMailsCount = rs.getInt("total_mails");

                    // 計算郵件送達率
                    double deliveryRate = totalMailsCount > 0 ?
                            (double) successfulMailsCount / totalMailsCount : 0.0;

                    // 將日期和對應的郵件送達率存儲在 Map 中
                    dailyDeliveryRates.put(sendDate, deliveryRate);
                }, DeliveryStatus.RECEIVE.name(), DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name(),
                account, startDate, endDate);

        return dailyDeliveryRates;
    }

    public Map<String, Integer> calculateMailConversionRate(String account) {
        Map<String, Integer> conversionRates = new LinkedHashMap<>();
        String sql = """
                SELECT
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS RECEIVE,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS FAILED,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS OPEN,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS CLICK
                FROM mail m
                LEFT JOIN campaign c ON m.campaign_id = c.id
                LEFT JOIN template t ON c.template_id = t.id
                LEFT JOIN company comp ON t.company_id = comp.id
                WHERE comp.account = ?
                AND m.send_date >= CURDATE() - INTERVAL 30 DAY
                """;

        jdbcTemplate.query(sql, rs -> {
            conversionRates.put("RECEIVE", rs.getInt("RECEIVE"));
            conversionRates.put("FAILED", rs.getInt("FAILED"));
            conversionRates.put("OPEN", rs.getInt("OPEN"));
            conversionRates.put("CLICK", rs.getInt("CLICK"));
        }, DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name(), DeliveryStatus.OPEN.name(), DeliveryStatus.CLICK.name(), account);
//        // 計算所有郵件的總數
//        int totalMails = conversionRates.values().stream().mapToInt(Integer::intValue).sum();
//        conversionRates.put("ALL", totalMails);
        // 計算 "ALL"，即 RECEIVE 和 FAILED 的總和
        int totalReceivedAndFailed = conversionRates.getOrDefault("RECEIVE", 0) + conversionRates.getOrDefault("FAILED", 0);
        conversionRates.put("ALL", totalReceivedAndFailed);
        return conversionRates;
    }

    public Map<String, Integer> calculateMailConversionRateByDate(String account, LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> conversionRates = new LinkedHashMap<>();
        String sql = """
                SELECT
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS RECEIVE,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS FAILED,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS OPEN,
                SUM(CASE WHEN m.status = ? THEN 1 ELSE 0 END) AS CLICK
                FROM mail m
                LEFT JOIN campaign c ON m.campaign_id = c.id
                LEFT JOIN template t ON c.template_id = t.id
                LEFT JOIN company comp ON t.company_id = comp.id
                WHERE comp.account = ?
                AND m.send_date >= ?
                AND m.send_date <= ?
                """;

        jdbcTemplate.query(sql, rs -> {
            conversionRates.put("RECEIVE", rs.getInt("RECEIVE"));
            conversionRates.put("FAILED", rs.getInt("FAILED"));
            conversionRates.put("OPEN", rs.getInt("OPEN"));
            conversionRates.put("CLICK", rs.getInt("CLICK"));
        }, DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name(), DeliveryStatus.OPEN.name(), DeliveryStatus.CLICK.name(), account, startDate, endDate);
        int totalReceivedAndFailed = conversionRates.getOrDefault("RECEIVE", 0) + conversionRates.getOrDefault("FAILED", 0);
        conversionRates.put("ALL", totalReceivedAndFailed);
        return conversionRates;
    }

    public Map<String, Map<LocalDate, Integer>> analyzeEventPastDays(String account, Integer days) {
        Map<String, Map<LocalDate, Integer>> eventAnalysis = new LinkedHashMap<>();

        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();

        for (String event : new String[]{"RECEIVE", "FAILED", "OPEN", "CLICK"}) {
            eventAnalysis.put(event, new LinkedHashMap<>());
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                eventAnalysis.get(event).put(date, 0);
            }
        }
        String sql = """
                SELECT 
                m.status,
                DATE(m.send_date) AS send_date,
                COUNT(*) AS count 
                FROM mail m
                LEFT JOIN campaign c ON m.campaign_id = c.id
                LEFT JOIN template t ON c.template_id = t.id
                LEFT JOIN company comp ON t.company_id = comp.id
                WHERE comp.account = ?
                AND send_date BETWEEN ? AND ?
                GROUP BY status, DATE(send_date)
                """;
        jdbcTemplate.query(sql, rs -> {
            String status = rs.getString("status");
            LocalDate sendDate = rs.getDate("send_date").toLocalDate();
            int count = rs.getInt("count");

//            //generate a new map once the event type is not exist in eventAnalysis.
//            eventAnalysis.computeIfAbsent(status,k->new LinkedHashMap<>()).put(sendDate,count);
            eventAnalysis.get(status).put(sendDate, count);
        }, account, startDate, endDate);
        return eventAnalysis;
    }

    public Map<String, Map<LocalDate, Integer>> analyzeEventPastByDate(String account, LocalDate startDate, LocalDate endDate) {
        Map<String, Map<LocalDate, Integer>> eventAnalysis = new LinkedHashMap<>();

        for (String event : new String[]{"RECEIVE", "FAILED", "OPEN", "CLICK"}) {
            eventAnalysis.put(event, new LinkedHashMap<>());
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                eventAnalysis.get(event).put(date, 0);
            }
        }
        String sql = """
                SELECT 
                m.status,
                DATE(m.send_date) AS send_date,
                COUNT(*) AS count 
                FROM mail m
                LEFT JOIN campaign c ON m.campaign_id = c.id
                LEFT JOIN template t ON c.template_id = t.id
                LEFT JOIN company comp ON t.company_id = comp.id
                WHERE comp.account = ?
                AND send_date BETWEEN ? AND ?
                GROUP BY status, DATE(send_date)
                """;
        jdbcTemplate.query(sql, rs -> {
            String status = rs.getString("status");
            LocalDate sendDate = rs.getDate("send_date").toLocalDate();
            int count = rs.getInt("count");
            eventAnalysis.get(status).put(sendDate, count);
        }, account, startDate, endDate);
        return eventAnalysis;
    }

    public List<Map<String, Object>> analyzeCampaignAudienceByAge(Long campaignId) {
        String sql = """
                SELECT 
                age_group,
                COUNT(*) AS total_engagement,
                SUM(CASE WHEN mail_status = 'OPEN' THEN 1 ELSE 0 END) AS open_count,
                SUM(CASE WHEN mail_status = 'CLICK' THEN 1 ELSE 0 END) AS click_count,
                SUM(CASE WHEN mail_status = 'OPEN' THEN 1 ELSE 0 END) / COUNT(CASE WHEN mail_status IN ('RECEIVE') THEN 1 END) AS open_rate,
                SUM(CASE WHEN mail_status = 'CLICK' THEN 1 ELSE 0 END) / COUNT(CASE WHEN mail_status IN ('RECEIVE') THEN 1 END) AS click_rate
                FROM (
                SELECT 
                a.id AS audience_id,
                TIMESTAMPDIFF(YEAR, STR_TO_DATE(a.birthday, '%Y-%m-%d'), CURDATE()) AS age,
                CASE 
                WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(a.birthday, '%Y-%m-%d'), CURDATE()) BETWEEN 0 AND 19 THEN '0-19'
                WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(a.birthday, '%Y-%m-%d'), CURDATE()) BETWEEN 20 AND 39 THEN '20-39'
                WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(a.birthday, '%Y-%m-%d'), CURDATE()) BETWEEN 40 AND 59 THEN '40-59'
                ELSE '60+'
                END AS age_group,
                m.status AS mail_status
                FROM audience a
                JOIN mail m ON a.id = m.audience_id
                WHERE m.campaign_id = ?
                ) AS age_data
                GROUP BY age_group;             
                """;
        List<Map<String, Object>> result = jdbcTemplate.query(sql, new Object[]{campaignId}, (rs, rowNum) -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("age", rs.getString("age_group"));
            map.put("total_engagement", rs.getInt("total_engagement"));
            map.put("open_count", rs.getInt("open_count"));
            map.put("click_count", rs.getInt("click_count"));
            map.put("open_rate", rs.getDouble("open_rate"));
            map.put("click_rate", rs.getDouble("click_rate"));
            return map;
        });
        int totalEngagement = result.stream().mapToInt(m -> (int) m.get("total_engagement")).sum();

        List<Map<String, Object>> response = new ArrayList<>();
        for (Map<String, Object> map : result) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("age", map.get("age"));
            entry.put("ratio", ((int) map.get("total_engagement") / (double) totalEngagement) * 100);
            Map<String, Double> type = new LinkedHashMap<>();
            type.put("OPEN", (((int) map.get("open_count")) / (double) totalEngagement) * 100);
            type.put("CLICK", (((int) map.get("click_count")) / (double) totalEngagement) * 100);
            entry.put("type", type);

            entry.put("open_rate", map.get("open_rate"));
            entry.put("click_rate", map.get("click_rate"));
            response.add(entry);
        }
        return response;
    }

    public List<Mail> trackFailedMailsByCampaignId(Long campaignId) {
        String sql = """
                SELECT * FROM mail WHERE status = ? AND campaign_id = ?;
                """;
        RowMapper<Mail> mapper = originMailRowMapper();
        try {
            return jdbcTemplate.query(sql, mapper, DeliveryStatus.FAILED.name(), campaignId);
        } catch (EmptyResultDataAccessException e) {
            log.error("error on getting failed mail of this campaign : " + e.getMessage());
            return null;
        }
    }

    public List<Mail> trackFailedMailsByCampaignIdWithPage(Long campaignId, int pageSize, int offset) {
//        String sql = """
//                SELECT * FROM mail
//                WHERE status = ? AND campaign_id = ?
//                LIMIT ? OFFSET ?;
//                """;
        String sql = """
                SELECT * FROM mail
                WHERE campaign_id = ?
                LIMIT ? OFFSET ?;
                """;
        RowMapper<Mail> mapper = originMailRowMapper();
        try {
//            return jdbcTemplate.query(sql, mapper, DeliveryStatus.FAILED.name(), campaignId, pageSize, offset);
            return jdbcTemplate.query(sql, mapper, campaignId, pageSize, offset);
        } catch (EmptyResultDataAccessException e) {
            log.error("error on getting failed mail of this campaign : " + e.getMessage());
            return null;
        }
    }

    public RowMapper<Mail> originMailRowMapper() {
        return new RowMapper<Mail>() {
            @Override
            public Mail mapRow(ResultSet rs, int rowNum) throws SQLException {
                Mail mail = new Mail();
                mail.setId(rs.getLong("id"));
                mail.setCampaignID(rs.getLong("campaign_id"));
                mail.setRecipientMail(rs.getString("recipient_mail"));
                mail.setSubject(rs.getString("subject"));
                mail.setStatus(rs.getString("status"));
                Date sendDateSQL = rs.getDate("send_date");
                LocalDate sendDate = sendDateSQL.toLocalDate();
                mail.setSendDate(sendDate);
                Timestamp timestamp = rs.getTimestamp("timestamp");
                mail.setTimestamp(timestamp);
                return mail;
            }
        };
    }
}
