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
public class ProductStatusVO {
    private String status_id;
    private String product_id;
    private boolean current_status;
    private LocalDateTime updated_at;
}
