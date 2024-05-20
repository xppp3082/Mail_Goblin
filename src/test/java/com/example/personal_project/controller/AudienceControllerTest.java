package com.example.personal_project.controller;

import com.example.personal_project.component.AuthenticationComponent;
import com.example.personal_project.component.MailConsumer;
import com.example.personal_project.config.SecurityConfig;
import com.example.personal_project.middleware.JwtTokenFilter;
import com.example.personal_project.service.AudienceService;
import com.example.personal_project.service.CompanyService;
import com.example.personal_project.utils.AudienceGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static com.example.personal_project.utils.AudienceGenerator.AUDIENCES_JSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(controllers = AudienceController.class, excludeFilters =
@ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {JwtTokenFilter.class}),
        excludeAutoConfiguration = {SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("consumer")
public class AudienceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AudienceService audienceService;
    @MockBean
    private CompanyService companyService;
    @MockBean
    private AuthenticationComponent authenticationComponent;
    @MockBean
    private MailConsumer mailConsumer;


    @BeforeEach
    public void init() {
        //Mock Security Context
        Authentication authentication = new UsernamePasswordAuthenticationToken("test12345@example.com", null, List.of());
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
        log.info("Security context set with authentication: {}", securityContext.getAuthentication());

        //Mock AuthenticationComponent
        when(authenticationComponent.getAccountFromAuthentication())
                .thenReturn("test12345@example.com");
    }

    @Test
    public void get_pagingAudience() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Current authentication: {}", authentication);
        assertNotNull(authentication);
        assertEquals("test12345@example.com", authentication.getName());

        when(audienceService.getPageAudienceWithTagsByAccount(anyString(), anyInt()))
                .thenReturn(AudienceGenerator.getMockAudiences());
        mockMvc.perform(
                        get("/api/1.0/audience/paging"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(AUDIENCES_JSON));
    }
}
