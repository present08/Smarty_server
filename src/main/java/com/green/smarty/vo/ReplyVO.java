package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyVO {
    private int reply_id;
    private int board_id;
    private String user_id;
    private String content;
    private LocalDateTime send_date;
}
