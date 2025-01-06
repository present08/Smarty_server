package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentVO {
    private String enrollment_id;
    private String user_id;
    private String class_id;
    private String enrollment_status;
}
