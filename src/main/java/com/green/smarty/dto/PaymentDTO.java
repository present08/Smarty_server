package com.green.smarty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class PaymentDTO {
    private String payment_id; // 결제 ID
    private String reservation_id; // 예약 ID
    private String enrollment_id; // 등록 ID
    @JsonProperty("user_id")
    private String user_id; // 사용자 ID
//    @JsonProperty("rental_id")
//    private String rental_id; // 대여 ID
    private float amount; // 결제 금액
    private boolean payment_status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate payment_date; // 결제 생성 날짜
}
