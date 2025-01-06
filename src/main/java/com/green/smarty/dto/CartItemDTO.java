package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CartItemDTO {
    private String product_id; // 상품 ID
    private int quantity;      // 수량
    private int price;         // 가격
    private String user_id;


}
