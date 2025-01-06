package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MembershipVO {
    private String membership_id;
    private String membership_level;
    private String user_id;
    private Timestamp last_reset_date;
    private String membership_status;
    private int used_benefit_count;
    private float total_payment_amount;
}
