package com.shopespher.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("Entering JwtAuthorizationFilter :: doFilterInternal method...");

        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }

            Claims claims = jwtUtil.resolveClaims(request);
            if (claims != null && jwtUtil.validateClaims(claims)) {
                String email = claims.getSubject();
                logger.info("Authenticated email: {}", email);

                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                Object rawRoles = claims.get("roles");

                if (rawRoles instanceof List<?>) {
                    for (Object roleObj : (List<?>) rawRoles) {
                        if (roleObj instanceof Map<?, ?> roleMap) {
                            Object roleName = roleMap.get("name");
                            if (roleName instanceof String nameStr) {
                                authorities.add(new SimpleGrantedAuthority(nameStr));
                            }
                        } else if (roleObj instanceof String roleStr) {
                            // fallback in case roles are already in List<String> format
                            authorities.add(new SimpleGrantedAuthority(roleStr));
                        }
                    }
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Authentication error", e);

            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("message", "Authentication Error");
            errorDetails.put("details", e.getMessage());

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            mapper.writeValue(response.getWriter(), errorDetails);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
