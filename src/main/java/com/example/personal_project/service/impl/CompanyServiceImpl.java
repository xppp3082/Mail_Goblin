package com.example.personal_project.service.impl;

import com.example.personal_project.model.Company;
import com.example.personal_project.model.form.SignInForm;
import com.example.personal_project.repository.CompanyRepo;
import com.example.personal_project.service.CompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepo companyRepo;

    public CompanyServiceImpl(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @Override
    public Long getIdByAccount(String account) {
        return companyRepo.getIdByAccount(account);
    }

    @Override
    public Company getCompanyByAccount(String account) {
        return companyRepo.getCompanyByAccount(account);
    }

    @Override
    public Company signUp(Company company) {
        try {
            String originPassword = company.getPassword();
            String encodedPassword = BCrypt.hashpw(originPassword, BCrypt.gensalt());
            UUID uuid = UUID.randomUUID();
            company.setCompanyUUID(uuid.toString());
            company.setPassword(encodedPassword);
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error on saving company in service layer : " + e.getMessage());
            throw e;
        }
        return companyRepo.insertNewCompany(company);
    }

    @Override
    public Company signIn(SignInForm signInForm) {
        String account = signInForm.getAccount();
        Company targetCompany = companyRepo.getCompanyByAccount(account);
        String encodePassword = targetCompany.getPassword();
        boolean passwordCorrect = BCrypt.checkpw(signInForm.getPassword(), encodePassword);
        if (passwordCorrect) {
            return targetCompany;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Integer> getCompanyProfileData(String account) {
        return companyRepo.getCompanyProfileData(account);
    }
}
