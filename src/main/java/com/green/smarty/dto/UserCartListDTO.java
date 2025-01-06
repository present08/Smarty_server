package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserCartListDTO {
    private String cart_id;        // 장바구니 ID
    private String user_id;      // 사용자 ID
    private String product_id;   // 상품 ID
    private String product_name; // 상품 이름
    private int quantity;       // 수량
    private LocalDateTime created_at; // 생성 시각
    private LocalDateTime updated_at; // 수정 시각
    private String file_name;
    private int price;
}
