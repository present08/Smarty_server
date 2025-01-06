package com.green.smarty.util;

import com.green.smarty.dto.SecurityUserDTO;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.vo.UserVO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JWTCheckFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserMapper userMapper;

    public JWTCheckFilter(JwtTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 요청 헤더에서 JWT 토큰 가져오기
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // JWT 검증 및 사용자 정보 가져오기
                Map<String, Object> claims = jwtTokenProvider.validateToken(token);
                String userId = (String) claims.get("id");
                List<String> roles = (List<String>) claims.get("role");
                UserVO userVO = userMapper.getById(userId);

                // SecurityUserDTO 생성 (UserDetails 구현체)
                SecurityUserDTO userDetails = new SecurityUserDTO(userVO);

                // Authentication 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // 권한 정보
                        );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContextHolder에 인증 정보 설정
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                logger.error("JWT 검증 실패: " + e.getMessage());
            }
        }

        // 다음 필터로 요청 전달
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 필터를 실행하지 않을 경로 설정
        String path = request.getRequestURI();
        return path.startsWith("/api/security");
    }
}

