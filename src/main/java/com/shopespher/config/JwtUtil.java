package com.shopespher.config;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final String secretKey = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCQWEDFGHJUY";
    private long accessTokenValidity = 5 * 60 * 60 * 1000; // 5 hours in milliseconds

    private final JwtParser jwtParser;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil() {
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

//    public String createToken(LoginRequest logInRequest, List<Role> roles) {
//        Optional<User> optionalUser = userRepository.findByEmail(logInRequest.getEmail());
//
//        if (optionalUser.isPresent()) {
//            User user = optionalUser.get();
//            String fullName = user.getFirstName() + " " + user.getLastName();
//            logger.debug("Entering JwtUtil :: createToken method...");
//
//            Claims claims = Jwts.claims().setSubject(logInRequest.getEmail());
//            claims.put("name", fullName);
//            claims.put("roles", roles); // Include roles in claims
//
//            Date tokenCreateTime = new Date();
//            Date tokenValidity = new Date(tokenCreateTime.getTime() + accessTokenValidity);
//
//            logger.debug("JwtUtil :: createToken method end...");
//
//            return Jwts.builder()
//                    .setClaims(claims)
//                    .setExpiration(tokenValidity)
//                    .signWith(SignatureAlgorithm.HS256, secretKey)
//                    .compact();
//        } else {
//            logger.warn("User  not found for email: " + logInRequest.getEmail());
//            throw new UsernameNotFoundException("User  not found with email: " + logInRequest.getEmail());
//        }
//    }


//    public String generateToken(SignUpRequest signUpRequest, List<String> roles) {
//        logger.debug("Entering JwtUtil :: generateToken method...");
//        Claims claims = Jwts.claims().setSubject(signUpRequest.getEmail());
//        claims.put("roles", roles); // Include roles in claims
//        Date tokenCreateTime = new Date();
//        Date tokenValidity = new Date(tokenCreateTime.getTime() + accessTokenValidity);
//        logger.debug("JwtUtil :: generateToken method end...");
//        return Jwts.builder()
//                .setClaims(claims)
//                .setExpiration(tokenValidity)
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }

    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            req.setAttribute("expired", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            req.setAttribute("invalid", ex.getMessage());
            throw ex;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }

    public String getEmail(Claims claims) {
        return claims.getSubject();
    }

    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }
}