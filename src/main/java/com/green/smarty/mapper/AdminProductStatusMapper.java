package com.green.smarty.mapper;

import com.green.smarty.vo.ProductStatusLogVO;
import com.green.smarty.vo.ProductStatusVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminProductStatusMapper {
    // 상품 상태 등록
    void insertProductStatus(ProductStatusVO status);

    // 특정 ID에 대한 최대 suffix 값 조회
    int findMaxSuffix(@Param("baseId") String baseId);

    // 중복된 status_id 존재 여부 확인
    boolean existsByStatusId(@Param("status_id") String status_id);

    // 특정 facility_id의 상품 상태 조회
    List<Map<String, Object>> findProductStatusByFacility(@Param("facilityId") String facilityId);

    // 상태 ID로 상품 정보 조회
    Map<String, Object> getProductInfoByStatusId(@Param("status_id") String statusId);

    // 대여 가능 여부 변경 (stock과 change_quantity 기반)
    void updateAvailability(@Param("statusId") String statusId, @Param("isAvailable") boolean isAvailable);

    // 변경된 상태 업데이트
    void updateChangedStatus(@Param("statusId") String statusId, @Param("changedStatus") String changedStatus);

    // 상태 복구
    void restoreToAvailable(@Param("statusId") String statusId);

    // 대여상품 수량 수정
    void updateProductStock(@Param("product_id") String productId, @Param("stock") int newStock);

    // 특정 product 상태별 수량 조회
    List<Map<String, Object>> findStatusCountsByProductId(@Param("productId") String productId);

    // updated_at 업데이트
    void updateProductStatusUpdatedAt(@Param("product_id") String productId);

    // 로그 관련 메서드
    void insertStatusLog(ProductStatusLogVO log);

    List<ProductStatusLogVO> findLogsByStatusId(@Param("statusId") String statusId);

    List<ProductStatusLogVO> findLogsByProductId(@Param("product_id") String productId);

    ProductStatusLogVO findLatestLogByStatusId(@Param("statusId") String statusId);

    void deleteLogByLogId(@Param("logId") Integer logId);

    // change_quantity 합계 계산
    int calculateTotalChangeQuantity(@Param("statusId") String statusId);
}