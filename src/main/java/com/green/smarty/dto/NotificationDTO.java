package com.green.smarty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String notification_id;
    private String user_id;
    private String message;
    private String status;              // 전송 상태 (SUCCESS or FAILURE)
    private String response_detail;      // 응답 메시지나 실패 이유
    private String user_name;
    private String message_type;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime send_date;
}

