package com.example.personal_project.repository;

import com.example.personal_project.model.status.DeliveryStatus;
import com.example.personal_project.model.Mail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleToLongFunction;

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
                ps.setLong(1, mail.getCompanyID());
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

    public void insertEventRecord(String campaignId, String eventType, String audienceUUId) {
        String sql = """
                INSERT INTO 
                mail (campaign_id,status,send_date,timestamp,audience_id)
                VALUES (?,?,?,?,(select id from audience where audience.audience_uuid =?));
                """;
        try {
            jdbcTemplate.update(sql,
                    campaignId, eventType,
                    LocalDate.now(), Timestamp.valueOf(LocalDateTime.now()), audienceUUId);
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
                    Integer.class, companyID, date,DeliveryStatus.RECEIVE.name(),DeliveryStatus.FAILED.name());

            // 計算郵件送達率
            double deliveryRate = totalMailsCount != null && totalMailsCount > 0 ?
                    (double) successfulMailsCount / totalMailsCount : 0.0;

            // 將日期和對應的郵件送達率存儲在 Map 中
            dailyDeliveryRates.put(date, deliveryRate);
        }

        return dailyDeliveryRates;
    }

    public  Map<LocalDate,Double> trackDailyMailDeliveryRate(String account){
        Map<LocalDate, Double> dailyDeliveryRates = new LinkedHashMap<>();
        LocalDate startDate = LocalDate.now().minusDays(30);
        // 初始化所有日期的成功率為 0.0
        for (int i = 0; i < 30; i++) {
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
        },  DeliveryStatus.RECEIVE.name(), DeliveryStatus.RECEIVE.name(), DeliveryStatus.FAILED.name(),
                account, startDate, LocalDate.now());

        return dailyDeliveryRates;
    }
}
