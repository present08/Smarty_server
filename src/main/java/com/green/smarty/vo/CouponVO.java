package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponVO {
    private String coupon_id;
    private String coupon_code;
    private String coupon_name;
    private LocalDateTime issue_date;
    private LocalDateTime expiry_date;
    private String user_id;
    private String status;
    private BigDecimal discount_rate;
}
