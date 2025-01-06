package com.green.smarty.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVO {

    private String product_id;          // 상품 ID
    private String facility_id;         // 시설 ID
    private String product_name;        // 상품명
    private String management_type;     // 관리 방식
    private String size;                // 사이즈 정보 (콤마로 구분된 문자열)
    private int stock;                  // 수량
    private int price;                  // 가격
    private boolean product_images;     // 이미지 유무
}
