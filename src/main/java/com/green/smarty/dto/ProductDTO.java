package com.green.smarty.dto;

import com.green.smarty.vo.ProductAttachVO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class ProductDTO {
    private String product_id;
    private String facility_id;
    private String product_name;
    private String management_type;
    private int price;
    private String facility_name;
    private boolean product_images;
    private int stock;
    private String size;
    private List<ProductAttachVO> attachFiles;
}
