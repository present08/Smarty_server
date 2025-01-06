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
public class BoardVO {
    private int board_id;
    private String title;
    private String content;
    private LocalDateTime send_date;
    private String content_type;
    private int view_count;
    private int good_btn;
    private int bad_btn;
    private String user_id;
    private LocalDateTime update_date;
    private int is_deleted;
    private LocalDateTime deleted_date;
}
