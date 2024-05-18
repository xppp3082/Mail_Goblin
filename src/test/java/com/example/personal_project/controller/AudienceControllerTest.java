package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.config.SecurityConfig;
import com.example.personal_project.middleware.JwtTokenFilter;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.utils.AudienceGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.example.personal_project.utils.AudienceGenerator.AUDIENCES_JSON;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AudienceController.class, excludeFilters =
@ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {JwtTokenFilter.class}),
        excludeAutoConfiguration = {SecurityConfig.class})
public class AudienceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AudienceService audienceService;
    @MockBean
    private CompanyService companyService;
    @MockBean
    private AuthenticationComponent authenticationComponent;

    @Test
    public void get_pagingAudience() throws Exception {
        when(audienceService.getPageAudienceWithTagsByAccount(anyString(), anyInt()))
                .thenReturn(AudienceGenerator.getMockAudiences());

        mockMvc.perform(
                        get("/api/1.0/audience/paging?number={number}", 0))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(AUDIENCES_JSON));
    }
}
