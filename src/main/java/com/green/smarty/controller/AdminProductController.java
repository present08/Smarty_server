package com.green.smarty.controller;

import com.green.smarty.dto.ProductAdminDTO;
import com.green.smarty.service.AdminProductService;
import com.green.smarty.service.AdminProductStatusService;
import com.green.smarty.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
public class AdminProductController {

    @Autowired
    AdminProductService adminProductService;

    @Autowired
    AdminProductStatusService adminProductStatusService;

    // 상품 등록 (JSON 데이터 처리)
    @PostMapping("/data")
    public ResponseEntity<List<String>> registerProductData(@RequestBody List<ProductAdminDTO> productList) {
        log.info("수신된 상품 리스트: {}", productList);

        List<String> allProductIds = new ArrayList<>();

        for (ProductAdminDTO product : productList) {
            // 상품 등록 후 모든 ProductVO 리스트 반환
            List<ProductVO> registeredProducts = adminProductService.registerProduct(product);

            // 상태 등록 호출 (각 ProductVO에 대해 처리)
            for (ProductVO productVO : registeredProducts) {
                adminProductStatusService.registerDefaultStatus(
                        productVO.getProduct_id(),
                        productVO.getManagement_type(),
                        productVO.getStock(),
                        productVO.getSize()
                );
                allProductIds.add(productVO.getProduct_id());
            }
        }

        return ResponseEntity.ok(allProductIds);
    }

    // 파일 업로드 처리
    @PostMapping("/files/{productId}")
    public ResponseEntity<String> uploadFiles(
            @PathVariable String productId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        log.info("파일 업로드 시작! 상품 ID: {}", productId);

        // 첨부파일 확인
        if (files == null || files.isEmpty()) {
            log.warn("첨부파일이 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("첨부파일이 없습니다.");
        }
        log.info("수신된 파일 개수: {}", files.size());
        files.forEach(file -> log.info("파일 이름: {}", file.getOriginalFilename()));

        try {
            adminProductService.uploadFiles(productId, files);
            return ResponseEntity.ok("파일 업로드 성공!");
        } catch (IOException e) {
            log.error("파일 업로드 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("파일 업로드 실패");
        }
    }

    @GetMapping("/{product_id}")
    public ResponseEntity<ProductVO> getProductById(@PathVariable String product_id) {
        log.info("상품 정보 조회 요청 - product_id: {}", product_id);
        try {
            ProductVO product = adminProductService.getProductById(product_id);
            log.info("조회된 상품 정보: {}", product);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            log.warn("상품 조회 실패 - product_id: {}, 이유: {}", product_id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("상품 조회 중 에러 발생 - product_id: {}, 에러: {}", product_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 상품 이미지 반환
    @GetMapping("/images/{file_name}")
    public ResponseEntity<Resource> showProductImage(@PathVariable(name = "file_name") String file_name) {
        log.info("상품 이미지 요청 - file_name: {}", file_name);
        try {
            return adminProductService.showProductImage(file_name);
        } catch (Exception e) {
            log.error("상품 이미지 조회 실패 - file_name: {}, 이유: {}", file_name, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 첨부파일 조회
    @GetMapping("/files/{product_id}")
    public ResponseEntity<List<String>> getProductFiles(@PathVariable String product_id) {
        log.info("첨부파일 조회 요청 - product_id: {}", product_id);
        try {
            List<String> files = adminProductService.getProductImageNames(product_id);
            log.info("조회된 첨부파일 목록: {}", files);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            log.error("첨부파일 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/files/{product_id}/{file_name}")
    public ResponseEntity<String> deleteProductImage(
            @PathVariable String product_id,
            @PathVariable String file_name
    ) {
        try {
            adminProductService.deleteProductImage(product_id, file_name);
            return ResponseEntity.ok("File deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File deletion failed.");
        }
    }
}
