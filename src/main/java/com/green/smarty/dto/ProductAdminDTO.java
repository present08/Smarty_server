package com.green.smarty.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductAdminDTO {

    private String product_id;
    private String facility_id;
    private String product_name;
    private String management_type;
    private int stock;
    private int price;
    private boolean product_images;

    @Builder.Default
    private List<String> size = new ArrayList<>(); // 기본값 빈 배열로 초기화

    @JsonSetter("size")
    public void setSize(Object size) {
        if (size == null || (size instanceof String && ((String) size).isEmpty())) {
            this.size = new ArrayList<>(); // 빈 값 처리
        } else if (size instanceof List) {
            this.size = (List<String>) size;
        } else {
            throw new IllegalArgumentException("Invalid size format");
        }
    }
}
