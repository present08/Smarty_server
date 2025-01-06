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
public class AnnounceVO {
    private int announce_id;
    private String content;
    private String title;
    private LocalDateTime send_date;
    private int isImportant;
    private int view_count;
}
