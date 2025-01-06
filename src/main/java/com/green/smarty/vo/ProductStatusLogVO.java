package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatusLogVO {
    private Integer log_id; // 로그 ID
    private String status_id; // `product_status`의 Foreign Key
    private String rental_id;
    private String changed_status; // 변경된 상태
    private Integer change_quantity; // 변경된 수량
    private LocalDateTime created_at; // 로그 생성 시간
}
