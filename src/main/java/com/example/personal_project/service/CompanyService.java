package com.example.personal_project.service;

import com.example.personal_project.model.Company;
import com.example.personal_project.model.form.SignInForm;

public interface CompanyService {
    Long getIdByAccount(String account);
    Company signUp(Company company);
    Company signIn(SignInForm signInForm);
    Company getCompanyByAccount(String account);
}
