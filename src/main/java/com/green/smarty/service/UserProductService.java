package com.green.smarty.service;

import com.green.smarty.mapper.UserProductAttachMapper;
import com.green.smarty.mapper.UserProductMapper;
import com.green.smarty.util.CustomFileUtil;
import com.green.smarty.vo.ProductAttachVO;
import com.green.smarty.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserProductService{
    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private CustomFileUtil customFileUtil;


    public List<ProductVO> getAllProducts() {
        List<ProductVO> productList = userProductMapper.getAllProducts();
        log.info("Products from service: {}", productList);
        return productList;
    }

    public ProductVO getProductById(String product_id) {
        log.info("Fetching Product With Id: {}", product_id);
        ProductVO vo = userProductMapper.getProductById(product_id);
        if (vo == null) {
            log.warn("Product Not Found With Id: {}", product_id);
        }
        return vo;
    }


    // 상품 이미지 파일 이름 조회
    public List<String> getProductImageNames(String product_id) {
        List<String> fileNames = userProductMapper.getProductImages(product_id);
        if (fileNames == null || fileNames.isEmpty()) {
            log.warn("상품 이미지가 없습니다. product_id: {}", product_id);
            return Collections.emptyList();
        }
        return fileNames;
    }

    public ResponseEntity<Resource> showProductImage(String file_name) {
        log.info("서비스 파일 조회 요청 - file_name: {}", file_name);

        // 파일 이름이 null 또는 빈 값일 경우 기본 이미지 반환
        if (file_name == null || file_name.trim().isEmpty()) {
            log.warn("파일 이름이 비어있습니다. 기본 이미지로 반환합니다.");
            file_name = "default.jpg";
        }

        try {
            // CustomFileUtil을 사용해 파일 반환
            return customFileUtil.getFile(file_name);
        } catch (Exception e) {
            log.error("파일 조회 중 오류 발생 - file_name: {}", file_name, e);
            throw new RuntimeException("파일 조회 중 오류 발생", e);
        }
    }
}
