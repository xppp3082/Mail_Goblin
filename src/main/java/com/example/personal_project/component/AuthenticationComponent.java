package com.example.personal_project.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationComponent {

    public String getAccountFromAuthentication() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        (UsernamePasswordAuthenticationToken) authentication;
                // retrieve user name
                return usernamePasswordAuthenticationToken.getName();
            } else {
                log.error("Unauthorized");
                return null;
            }
        } catch (Exception e) {
            String errorMessage = "error on getting company account from middleware context holder.";
            log.error(errorMessage);
            return errorMessage;
        }
    }
}
