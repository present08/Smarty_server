package com.green.smarty.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVO {
    private String payment_id;
    private String reservation_id;
    private String enrollment_id;
    private String user_id;
//    private String rental_id;
    private float amount;
    private boolean payment_status; // 1 : 결제완료 , 0 : 환불

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime payment_date;
}
