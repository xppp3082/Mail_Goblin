package com.example.personal_project.middleware;

import com.example.personal_project.model.Company;
import com.example.personal_project.model.dto.CompanyDto;
import com.example.personal_project.model.response.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyStore;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {
    private Key secretKey;
    @Value("${jwt.signKey}")
    private String jwtSignKey;
    @Value("${jwt.expireTimeAsSec}")
    private Long jwtExpireTimeAsSec;
    private JwtParser jwtParser;

    @PostConstruct
    private void init() {
        byte[] customKeyBytes = jwtSignKey.getBytes(StandardCharsets.UTF_8);
        secretKey = Keys.hmacShaKeyFor(customKeyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    public Claims parseToken(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public LoginResponse createToken(Company company) {
        CompanyDto companyDto = CompanyDto.from(company);
        String accessToken = createAccessToken(company);
        LoginResponse res = new LoginResponse();
        res.setAccess_token(accessToken);
        res.setAccess_expired(jwtExpireTimeAsSec);
        res.setCompanyDto(companyDto);
        return res;
    }
    public String createAccessToken(Company company) {
        //有效時間(毫秒)
        long expirationMillis = Instant.now().plusSeconds(jwtExpireTimeAsSec).getEpochSecond() * 1000;
        //設置標準內容與自定義內容
        Claims claims = Jwts.claims();
        claims.setSubject(company.getAccount());
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(expirationMillis));
        claims.put("company", company);
        claims.put("title",company.getTitle());
        claims.put("account", company.getAccount());
        return Jwts.builder().
                setClaims(claims).
                signWith(secretKey).
                compact();

    }
}