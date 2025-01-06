package com.green.smarty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Builder
public class AdminEnrollmentDTO {
    // 수강신청 정보
    private String enrollment_id;
    private String class_id;
    private String class_name;
    private int class_size;
    private int register;
    private int attendance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate start_date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate end_date;
}
