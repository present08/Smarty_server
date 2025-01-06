package com.green.smarty.mapper;

import com.green.smarty.dto.ProductRentalMyPageUserDTO;
import com.green.smarty.dto.ProductRentalUserDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.vo.RentalVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param; // 수정: Spring Data의 @Param을 MyBatis용 @Param으로 변경

import java.util.List;
import java.util.Map;

@Mapper
public interface UserRentalMapper {
    // 대여 등록
    int insertRental(RentalVO vo);

    // 모든 대여 조회
    List<RentalDTO> getAllRentals();

    // 특정 대여 조회
    RentalDTO getRentalById(String rental_id);

    // 대여 가능한 status_id 조회
    int getAvailableQuantity(@Param("product_id") String productId);

    List<String> getAvailableStatusIds(@Param("product_id") String productId, @Param("count") int count);

    // 대여된 status_id 조회 (반납 시 사용)
    List<String> getRentedStatusIds(@Param("product_id") String productId,
                                    @Param("rental_id") String rentalId,
                                    @Param("count") int count);


    // 특정 상태의 총 감소 수량 계산
    int getTotalUnavailableQuantity(@Param("product_id") String productId);

    // 대여 상태 변경
    int updateChangedStatus(@Param("status_id") String statusId, @Param("changed_status") String changedStatus);

    // 대여 로그 추가 (rental_id 포함)
    int insertRentalLogWithRentalId(Map<String, Object> logData);

    // 대여 로그 삭제
    int deleteRentalLog(@Param("status_id") String statusId, @Param("rental_id") String rentalId);

    // 반납 상태 복구
    int restoreToAvailable(@Param("status_id") String statusId);

    // 특정 사용자 대여 목록 조회
    List<ProductRentalUserDTO> getUserRentalListData(String userId);

    // 반납 처리
    int returnRental(RentalVO vo);

    // 추가: 결제 상태 업데이트
    int updatePaymentStatus(Map<String, Object> map);

    // 추가: 날짜 기반 최대 Rental ID 조회
    String getMaxRentalIdForDate(@Param("datePrefix") String datePrefix);

    // 추가: 기간 지난 렌탈 목록 조회
    List<RentalDTO> getOverdueRentals();

    // 추가: 사용자 이메일 조회 (user_id 기준)
    String getEmailByUserId(String user_id);

    int updateCurrentStatus(@Param("product_id") String productId);

    String getProductNameByProductId(@Param("product_id") String productId);

}
