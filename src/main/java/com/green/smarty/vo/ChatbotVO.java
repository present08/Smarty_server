package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotVO {
    private String chat_room;
    private String user_id;
    private String question;
    private String answer;
    private String message;
    private boolean chatbot_status;
}
