package com.green.smarty.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class PermissionDTO {
    private String Permission_id; // 결제 ID
    private String enrollment_id; // 등록 ID
    private String user_id;
    private String user_name;
    private String class_name;
    private float amount; // 결제 금액
    private String enrollment_status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start_date; // 결제 생성 날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end_date; // 결제 생성 날짜
}
