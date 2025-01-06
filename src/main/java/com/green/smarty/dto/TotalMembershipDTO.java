package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class TotalMembershipDTO {
    private String user_id;
    private String enrollment_id;
    private String reservation_id;
    private String rental_id;
    private float amount;
}
