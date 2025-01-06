package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SecurityResponseDTO {
    private String token; // JWT 토큰
    private String userName; // 사용자 이름
    private String userId; // 사용자 ID
    private String role; // 사용자 권한 (ROLE_USER, ROLE_ADMIN 등)
}
