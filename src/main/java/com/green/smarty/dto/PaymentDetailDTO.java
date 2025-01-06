package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class PaymentDetailDTO {
    private String rental_id;
    private String reservation_id;
    private String enrollment_id;
    private float amount;
    private String user_id;
    private String product_id;
    private int count;
    private List<CartItemDTO> items;
}
