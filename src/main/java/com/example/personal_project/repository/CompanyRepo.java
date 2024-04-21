package com.example.personal_project.repository;

import com.example.personal_project.model.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDate;

@Slf4j
@Repository
@Transactional
public class CompanyRepo {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public Company insertNewCompany(Company company){
        String sql = """
                INSERT INTO company 
                (title, description, industry, company_uuid, anniversary, account, password) 
                VALUES 
                (?,?,?,?,?,?,?);
                """;
        try{
            KeyHolder keyHolder =new GeneratedKeyHolder();
            jdbcTemplate.update(connection ->{
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1,company.getTitle());
                ps.setString(2,company.getDescription());
                ps.setString(3,company.getIndustry());
                ps.setString(4,company.getCompanyUUID());
                ps.setDate(5, new Date(company.getAnniversary().getTime()));
                ps.setString(6,company.getAccount());
                ps.setString(7,company.getPassword());
                return ps;
            },keyHolder);
            Long generatedId = keyHolder.getKey().longValue();
            company.setId(generatedId);
            return company;
        }catch (DuplicateKeyException e){
            log.error("Duplicate account error on creating new company in Repo layer: " + e.getMessage());
            throw new DuplicateKeyException("Account already exists.");
        }
        catch (RuntimeException e){
            log.error("Error on creating new company in Repo layer : " + e.getMessage());
            throw  new RuntimeException("Failed to create company.");
        }
    }

    public Long getIdByAccount(String account) {
        String sql = "SELECT id FROM company WHERE account = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, account);
        } catch (EmptyResultDataAccessException e) {
            return null; // or handle the case where no company is found for the given account
        } catch (DataAccessException e) {
            log.error("Error retrieving company ID for account " + account + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve company ID for account " + account);
        }
    }

    public Company getCompanyByAccount(String account) {
        String sql = "SELECT * FROM company WHERE account = ?";
        try {
            return jdbcTemplate.queryForObject(sql, originCompanyRowMapper(), account);
        } catch (EmptyResultDataAccessException e) {
            return null; // or handle the case where no company is found for the given account
        } catch (DataAccessException e) {
            log.error("Error retrieving company for account " + account + ": " + e.getMessage());
            throw new RuntimeException("Failed to retrieve company for account " + account);
        }
    }


    public RowMapper<Company> originCompanyRowMapper(){
        return  new RowMapper<Company>() {
            @Override
            public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
                Company company = new Company();
                company.setId(rs.getLong("id"));
                company.setTitle(rs.getString("title"));
                company.setDescription(rs.getString("description"));
                company.setIndustry(rs.getString("industry"));
                company.setCompanyUUID(rs.getString("company_uuid"));
                Date anniversarySQL = rs.getDate("anniversary");
                company.setAnniversary(anniversarySQL);
                company.setAccount(rs.getString("account"));
                company.setPassword(rs.getString("password"));
                return company;
            }
        };
    }
}
