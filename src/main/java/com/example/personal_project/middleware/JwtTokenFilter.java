package com.example.personal_project.middleware;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static io.micrometer.common.util.StringUtils.isEmpty;

public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final ObjectMapper jsonObjectMapper = new ObjectMapper();
    public static final Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            final String token = retrieveToken(request);
            if(token ==null || !jwtTokenUtil.validate(token)){
                filterChain.doFilter(request,response);
                return;
            }

        }catch (Exception e){
            Map<String, String> errorMsg = new HashMap<>();
            errorMsg.put("error", e.getMessage());
            handleException(response, HttpStatus.UNAUTHORIZED.value(),errorMsg);
        }
    }

    @Nullable
    public String retrieveToken(HttpServletRequest request) {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (isEmpty(header) || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.split(" ")[1].trim();
    }

    private void handleException(HttpServletResponse response, int status, Map<String, String> message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        jsonObjectMapper.writeValue(response.getWriter(), message);
    }

}
