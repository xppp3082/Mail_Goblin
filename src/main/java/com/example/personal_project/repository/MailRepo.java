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
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
public class MailRepo {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertBatch(List<Mail> mails){
        String sql = """
                INSERT INTO 
                mail (campaign_id,recipient_mail,subject,status,send_date,timestamp,checktimes) 
                VALUES 
                (?,?,?,?,?,?,?);
                """;
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Mail mail = mails.get(i);
                //Set values for prepareStatement
                ps.setLong(1,mail.getCompanyID());
                ps.setString(2,mail.getRecipientMail());
                ps.setString(3, mail.getSubject());
                ps.setString(4, mail.getStatus());
                ps.setString(5,mail.getSendDate().toString());
                ps.setString(6, mail.getTimestamp().toString());
                ps.setInt(7,mail.getCheckTimes());
            }

            @Override
            public int getBatchSize() {
                return mails.size();
            }
        });
    }

}
