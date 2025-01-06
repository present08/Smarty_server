package com.green.smarty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class RentalDTO {
    private String rental_id; //대여ID
    private String user_id; //사용자ID
    private String product_id; //상품ID
    private boolean rental_status; // 대여상태(true: 대여 중, false: 반납 완료)
    private boolean payment_status;
    private int count;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rental_date; // 대여일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime return_date; // 반납일


}
