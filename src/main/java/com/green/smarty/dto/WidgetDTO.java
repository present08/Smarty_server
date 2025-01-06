package com.green.smarty.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WidgetDTO {
    private String enrollment_id;
    private String reservation_id;
    private String payment_id;
    private String user_id;
    private float amount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
    private LocalDateTime payment_date;
    private boolean payment_status;
    private String facility_name;
    private String court_name;
    private String class_name;
    private String product_name;
}
