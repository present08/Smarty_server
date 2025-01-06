package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
    public class CommunityDTO {
        private int id;               // 게시글 ID (board_id 또는 announce_id)
        private String title;         // 제목
        private String content;       // 내용
        private LocalDateTime send_date; // 생성 날짜
        private String content_type;  // 게시글 유형 ('announcement' 또는 board의 content_type 값)
        private int view_count;       // 조회수
        private String source;        // 데이터 출처 ('board' 또는 'announce')
    }
