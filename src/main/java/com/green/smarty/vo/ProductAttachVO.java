package com.green.smarty.vo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class ProductAttachVO {
    private String origin_path;
    private String thumbnail_path;
    private String file_name;
    private String product_id;
}
