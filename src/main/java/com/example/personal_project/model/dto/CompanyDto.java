package com.example.personal_project.model.dto;

import com.example.personal_project.model.Company;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyDto {
    private Long id;
    private String title;
    private String description;
    private String industry;
    private Date anniversary;
    private String account;

    public static CompanyDto from (Company company){
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(company.getId());
        companyDto.setTitle(company.getTitle());
        companyDto.setDescription(company.getDescription());
        companyDto.setIndustry(company.getIndustry());
        companyDto.setAnniversary(company.getAnniversary());
        companyDto.setAccount(company.getAccount());
        return companyDto;
    }
}
