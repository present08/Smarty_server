package com.green.smarty.controller;

import com.green.smarty.dto.SecurityResponseDTO;
import com.green.smarty.service.SecurityService;
import com.green.smarty.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserVO userVO) {
        try {
            return securityService.register(userVO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody UserVO userVO) {
        return securityService.login(userVO.getUser_id(), userVO.getPassword());
    }

    // 로그인 상태 확인
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = request.getHeader("Authorization");

        // Authorization 헤더에서 "Bearer "를 제거하고 토큰만 추출
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 제거
        } else {
            // 토큰이 없거나 형식이 잘못된 경우
            Map<String, Object> response = new HashMap<>();
            response.put("isLoggedIn", false);
            response.put("message", "No token provided or invalid token format");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            // 서비스 호출하여 토큰 검증 및 사용자 정보 추출
            Map<String, Object> userInfo = securityService.checkLoginStatus(token);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            // 토큰이 유효하지 않은 경우
            Map<String, Object> response = new HashMap<>();
            response.put("isLoggedIn", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
