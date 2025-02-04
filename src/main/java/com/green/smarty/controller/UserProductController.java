package com.green.smarty.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.dto.ProductDTO;
import com.green.smarty.mapper.UserProductMapper;
import com.green.smarty.service.UserProductService;
import com.green.smarty.vo.ProductVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user/products")

public class UserProductController {
    @Autowired
    private UserProductService service;
    @Autowired
    private UserProductMapper productMapper;
    @Value("upload")
    private String uploadPath;

    // product Data 전달
    @GetMapping("/")
    public List<ProductDTO> getProduct() {
        List<ProductDTO> productList = productMapper.getProductDTO();
        // log.info("Products from controller: {}", productList);
        System.out.println("Product 전달된 값 : " + productList);
        return productList;
    }

    @GetMapping("/detail/{product_id}")
    public ResponseEntity<ProductVO> getProductById(@PathVariable String product_id) {
        try {
            ProductVO product = service.getProductById(product_id);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("상품 조회 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{file_name}")
    public ResponseEntity<Resource> showProductImage(@PathVariable(name = "file_name") String file_name) {
        log.info("상품 이미지 요청 - file_name: {}", file_name);
        try {
            return service.showProductImage(file_name);
        } catch (Exception e) {
            log.error("상품 이미지 조회 실패 - file_name: {}, 이유: {}", file_name, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/files/{product_id}")
    public ResponseEntity<List<String>> getProductFiles(@PathVariable String product_id) {
        log.info("첨부파일 조회 요청 - product_id: {}", product_id);
        try {
            List<String> files = service.getProductImageNames(product_id);
            log.info("조회된 첨부파일 목록: {}", files);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("첨부파일 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
