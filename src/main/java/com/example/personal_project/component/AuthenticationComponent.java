package com.example.personal_project.component;

import com.example.personal_project.model.Audience;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AuthenticationComponent {

    public String getAccountFromAuthentication(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        (UsernamePasswordAuthenticationToken) authentication;
                // 獲取使用者名稱
                return usernamePasswordAuthenticationToken.getName();
            } else {
                // 如果不是 UsernamePasswordAuthenticationToken，表示未驗證或不明的身份，可以進行相應處理
                log.error("Unauthorized");
                return null;
            }
        }catch (Exception e){
            String errorMessage = "error on getting company account from middleware context holder.";
            log.error(errorMessage);
            return errorMessage;
        }
    }
}
