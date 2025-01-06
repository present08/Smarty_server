package com.green.smarty.service;

import com.green.smarty.dto.ProductAdminDTO;
import com.green.smarty.mapper.AdminProductMapper;
import com.green.smarty.util.CustomFileUtil;
import com.green.smarty.vo.ProductVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Log4j2
public class AdminProductService {
    @Autowired
    private AdminProductMapper adminProductMapper;

    @Autowired
    private CustomFileUtil customFileUtil;

    public List<ProductVO> registerProduct(ProductAdminDTO productAdminDTO) {
        List<String> sizes = productAdminDTO.getSize(); // 사이즈 리스트 가져오기
        List<ProductVO> registeredProducts = new ArrayList<>();

        if (sizes != null && !sizes.isEmpty()) {
            for (String size : sizes) {
                // 사이즈별 ProductVO 생성
                ProductVO productVO = toProductVO(productAdminDTO);
                productVO.setSize(size); // 현재 사이즈 설정

                // `generateProductId`를 사용하여 product_id 생성
                String productId = generateProductId(productVO.getFacility_id());
                productVO.setProduct_id(productId);

                // 상품 삽입
                adminProductMapper.insertProduct(productVO);
                registeredProducts.add(productVO);

                log.info("등록된 상품 (사이즈별): {}", productVO);
            }
        } else {
            // 사이즈가 없는 경우 기존 방식으로 처리
            ProductVO productVO = toProductVO(productAdminDTO);
            String productId = generateProductId(productVO.getFacility_id());
            productVO.setProduct_id(productId);

            // 상품 삽입
            adminProductMapper.insertProduct(productVO);
            registeredProducts.add(productVO);

            log.info("등록된 상품 (사이즈 없음): {}", productVO);
        }

        // 모든 등록된 ProductVO 리스트 반환
        return registeredProducts;
    }

    // 파일 업로드 처리
    public void uploadFiles(String productId, List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            log.info("첨부파일 없음, 파일 처리 건너뜀.");
            return;
        }

        Map<String, List<String>> filesInfo = customFileUtil.saveFiles(files);
        List<String> originPaths = filesInfo.get("origin_path");
        List<String> thumbnailPaths = filesInfo.get("thumbnail_path");
        List<String> fileNames = filesInfo.get("file_name");

        for (int i = 0; i < fileNames.size(); i++) {
            adminProductMapper.fileUpload(
                    productId,
                    originPaths.get(i),
                    thumbnailPaths.get(i),
                    fileNames.get(i)
            );
        }
    }

    private String generateProductId(String facilityId) {
        int index = 1;
        String baseId = facilityId.substring(facilityId.length() - 4);
        String productId;

        do {
            productId = "p_" + baseId + String.format("%02d", index++);
        } while (adminProductMapper.existsByProductId(productId));

        return productId;
    }

    private ProductVO toProductVO(ProductAdminDTO dto) {
        return ProductVO.builder()
                .facility_id(dto.getFacility_id())
                .product_name(dto.getProduct_name())
                .management_type(dto.getManagement_type().trim()) // 공백 제거
                .price(dto.getPrice())
                .size(dto.getSize() != null ? String.join(",", dto.getSize()) : null)
                .stock(dto.getStock())
                .product_images(dto.isProduct_images())
                .build();
    }

    public List<ProductAdminDTO> getProductsByFacilityId(String facilityId) {
        List<ProductVO> productVOs = adminProductMapper.getProductsByFacilityId(facilityId);
        List<ProductAdminDTO> productAdminDTOs = new ArrayList<>();

        for (ProductVO productVO : productVOs) {
            productAdminDTOs.add(toProductAdminDTO(productVO));
        }
        return productAdminDTOs;
    }

    public ProductVO getProductById(String product_id) {
        ProductVO product = adminProductMapper.getProductById(product_id);
        if (product == null) {
            throw new RuntimeException("상품을 찾을 수 없습니다. product_id: " + product_id);
        }
        return product;
    }

    // 상품 이미지 파일 이름 조회
    public List<String> getProductImageNames(String product_id) {
        List<String> fileNames = adminProductMapper.getProductImages(product_id);
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

    public void deleteProductImage(String product_id, String file_name) {
        adminProductMapper.deleteProductImage(product_id, file_name); // DB에서 파일 기록 삭제
        customFileUtil.deleteFiles(Collections.singletonList(file_name)); // 파일 삭제
    }


    private ProductAdminDTO toProductAdminDTO(ProductVO vo) {
        return ProductAdminDTO.builder()
                .product_id(vo.getProduct_id())
                .facility_id(vo.getFacility_id())
                .product_name(vo.getProduct_name())
                .management_type(vo.getManagement_type())
                .price(vo.getPrice())
                .size(vo.getSize() != null ? List.of(vo.getSize().split(",")) : new ArrayList<>())
                .stock(vo.getStock())
                .product_images(vo.isProduct_images())
                .build();
    }
}
