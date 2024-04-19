package com.example.personal_project.controller;

import com.example.personal_project.middleware.JwtTokenUtil;
import com.example.personal_project.model.Company;
import com.example.personal_project.model.form.SignInForm;
import com.example.personal_project.model.response.LoginResponse;
import com.example.personal_project.service.CompanyService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/1.0/company")
public class CompanyController {
    private final CompanyService companyService;
    private final JwtTokenUtil jwtTokenUtil;

    public CompanyController(CompanyService companyService, JwtTokenUtil jwtTokenUtil) {
        this.companyService = companyService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> companySignUp(@RequestBody Company company) {
        try {
            Company companySaved = companyService.signUp(company);
            LoginResponse loginResponse = jwtTokenUtil.createToken(company);
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (DuplicateKeyException e) {
            return ResponseEntity.badRequest().body("Account already exists.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create company.");
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> companySignIn(@RequestBody SignInForm signInForm) {
        try {
            Company targetCompany = companyService.signIn(signInForm);
            LoginResponse loginResponse = jwtTokenUtil.createToken(targetCompany);
            return new ResponseEntity<>(loginResponse, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sign-in fail, plz check your sign-in info.");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profileFromToken(
            @RequestHeader(value = "Authorization", required = false) String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            return new ResponseEntity<>("Token might be null or invalid", HttpStatus.UNAUTHORIZED);
        }
        try {
            String token = bearerToken.substring(7);//也可以從空白鍵開始切
            Claims claims = jwtTokenUtil.parseToken(token);
            return new ResponseEntity<>(claims, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Wrong token error", HttpStatus.FORBIDDEN);
        }
    }
}
