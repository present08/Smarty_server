package com.green.smarty.util;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = "uskmyouenjghejlaksnyuhomyjaengkuomhesynusohkjmjue";
    private final long EXPIRATION_TIME = 3600000; // 1시간

    // JWT 생성
    public String createToken(Map<String, Object> claims) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(claims.get("id").toString())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // JWT에서 사용자 ID 추출
    public String getUserId(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 유효성 검증
    public Map<String, Object> validateToken(String token) throws Exception {
        try {
            // JWT 파싱 및 검증
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 반환할 데이터를 Map에 담아 반환
            Map<String, Object> claimMap = new HashMap<>();
            claimMap.put("id", claims.get("id"));
            claimMap.put("email", claims.get("email"));
            claimMap.put("level", claims.get("level"));
            claimMap.put("role", claims.get("role"));
            return claimMap;

        } catch (ExpiredJwtException e) {
            throw new Exception("Token expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new Exception("Invalid token");
        }
    }
}
