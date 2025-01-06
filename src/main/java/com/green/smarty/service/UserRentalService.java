package com.green.smarty.service;

import com.green.smarty.dto.ProductRentalUserDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.mapper.*;
import com.green.smarty.vo.ProductVO;
import com.green.smarty.vo.RentalVO;
import com.green.smarty.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserRentalService {
    @Autowired
    private UserRentalMapper userRentalMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private PublicMapper publicMapper;
    //대여
    //수정 전
    public int insertRental(RentalVO vo, int count) {
        System.out.println("RentalVO 데이터 확인 : " + vo);
        System.out.println("Count 데이터 확인 : " + count);
        log.info("대여 등록 시작: {}", vo);

        if (vo.getRental_id() == null || vo.getRental_id().isEmpty()) {
            LocalDateTime date = LocalDateTime.now(); // 현재 날짜 가져오기
            List<RentalVO> rentalVOList = publicMapper.getRentalAll(); // 모든 렌탈 데이터 조회
            List<RentalVO> rentalList = new ArrayList<>();

            for (RentalVO item : rentalVOList) {
                String itemDate = item.getRental_id().substring(2, 10); // rental_id의 날짜 부분 추출
                System.out.println(itemDate);
                // 현재 날짜와 일치하는 rental_id를 리스트에 추가
                if (itemDate.equals("" + date.getYear() + date.getMonthValue() +
                        (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()))) {
                    rentalList.add(item);
                }
            }

            // rental_id 생성
            String rentalId = "R_" + date.getYear() + date.getMonthValue() +
                    (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) +
                    String.format("%03d", rentalList.size() + 1);
            vo.setRental_id(rentalId);
            System.out.println("rental ID : " + rentalId);
            log.info("rental_id 생성 완료: {}", rentalId);
        }

        // 1. 사용자 존재 여부 확인
        UserVO user = userMapper.getById(vo.getUser_id());
        if (user == null) {
            throw new RuntimeException("존재하지 않는 사용자입니다. ID: " + vo.getUser_id());
        }

        // 2. 상품 존재 여부 확인
        ProductVO product = userProductMapper.getProductById(vo.getProduct_id());
        if (product == null) {
            throw new RuntimeException("존재하지 않는 상품입니다. ID: " + vo.getProduct_id());
        }

        // 3. 대여 가능 여부 확인
        int availableQuantity = userRentalMapper.getAvailableQuantity(vo.getProduct_id());
        log.info("대여 가능 여부 확인: product_id={}, availableQuantity={}, 요청 수량={}",
                vo.getProduct_id(), availableQuantity, count);

        if (availableQuantity < count || availableQuantity <= 0) {
            throw new RuntimeException("대여 불가능: 남은 수량=" + availableQuantity +
                    ", 요청 수량=" + count + ", 상품 ID=" + vo.getProduct_id());
        }

        // 4. 날짜 확인
        if (vo.getRental_date() == null) {
            vo.setRental_date(LocalDateTime.now()); // 기본 대여 날짜 설정
        }

        // 5. 대여 상태 설정
        vo.setRental_status(true);
        vo.setCount(count);

        // 6. 대여 정보 저장
        int rentalId = userRentalMapper.insertRental(vo);
        log.info("렌탈 데이터 저장 완료: Rental ID={}", rentalId);

        // 7. 대여 로그 기록
        insertRentalLog(vo.getRental_id(), vo.getProduct_id(), count);

        return rentalId;
    }

//    public void insertRentals(List<RentalVO> rentals) {
//        for (RentalVO rental : rentals) {
//            System.out.println("insertRentals 내부: RentalVO 데이터 확인 - " + rental); // 추가 확인
//            insertRental(rental, rental.getCount());
//        }
//    }

    public void insertRentals(List<RentalVO> rentals) {
        for (RentalVO rental : rentals) {
            userRentalMapper.insertRental(rental);
            System.out.println("rentalService의 insertRentals 데이터 확인 : "+rental);
        }
    }

    // 새로운 메서드: 로그 기록
    private void insertRentalLog(String rentalId, String productId, int count) {
        log.info("대여 로그 기록 시작: rentalId={}, productId={}, count={}", rentalId, productId, count);

        // 1. 대여 가능한 `status_id` 조회
        List<String> statusIds = userRentalMapper.getAvailableStatusIds(productId, count);
        if (statusIds.isEmpty()) {
            throw new RuntimeException("대여 가능한 상태 ID가 부족합니다. productId=" + productId);
        }

        // 2. 개별 관리 여부 확인
        boolean isIndividualManaged = statusIds.size() > 1;
        log.info("관리 방식: {}", isIndividualManaged ? "개별 관리" : "일괄/사이즈별 관리");

        // 3. 관리 방식에 따라 처리
        if (isIndividualManaged) {
            // 개별 관리: 각 상태 ID별로 로그 생성
            for (int i = 0; i < count; i++) {
                Map<String, Object> logData = new HashMap<>();
                logData.put("rental_id", rentalId);
                logData.put("status_id", statusIds.get(i)); // 개별 상태 ID
                logData.put("changed_status", "대여 중");
                logData.put("change_quantity", 1);

                try {
                    userRentalMapper.insertRentalLogWithRentalId(logData); // 로그 삽입
                    userRentalMapper.updateChangedStatus(statusIds.get(i), "대여 중"); // 상태 변경
                    log.info("개별 관리 로그 생성 완료: {}", logData);
                } catch (Exception e) {
                    log.error("Failed to insert rental log or update status: {}", logData, e);
                    throw e;
                }
            }
        } else {
            // 일괄 관리와 사이즈별 관리: 단일 상태 ID로 처리
            Map<String, Object> logData = new HashMap<>();
            logData.put("rental_id", rentalId);
            logData.put("status_id", statusIds.get(0)); // 단일 상태 ID
            logData.put("changed_status", "대여 중");
            logData.put("change_quantity", count); // 전체 수량

            try {
                userRentalMapper.insertRentalLogWithRentalId(logData); // 로그 삽입
                userRentalMapper.updateChangedStatus(statusIds.get(0), "대여 중"); // 상태 변경
                log.info("일괄/사이즈별 관리 로그 생성 완료: {}", logData);
            } catch (Exception e) {
                log.error("Failed to insert rental log or update status: {}", logData, e);
                throw e;
            }
        }

        // 4. 상품 상태 업데이트
        userRentalMapper.updateCurrentStatus(productId);
        log.info("전체 상태 업데이트 완료: productId={}", productId);
    }


    // 반납
    public int returnRental(String rental_id, int count) {

        // 1. 대여 정보 확인
        RentalDTO rental = userRentalMapper.getRentalById(rental_id);
        if (rental == null) {
            throw new RuntimeException("대여 정보가 존재하지 않습니다. Rental ID: " + rental_id);
        }

        if (!rental.isRental_status()) {
            throw new RuntimeException("이미 반납된 대여입니다. Rental ID: " + rental_id);
        }
        // 2. 반납 데이터 업데이트
        RentalVO rentalVO = new RentalVO();
        rentalVO.setRental_id(rental_id);
        rentalVO.setRental_status(false);
        rentalVO.setReturn_date(LocalDateTime.now());
        int result = userRentalMapper.returnRental(rentalVO);

        if (result <= 0) {
            throw new RuntimeException("반납 처리 중 오류가 발생했습니다. Rental ID: " + rental_id);
        }
        log.info("반납 데이터 업데이트 완료: Rental ID={}", rental_id);

        // 3. 반납 처리 - 로그 삭제 및 상태 복구
        processReturnLogs(rental_id, rental.getProduct_id(), count);

        return result;
    }

    private void processReturnLogs(String rentalId, String productId, int count) {
        log.info("반납 로그 처리 시작: rentalId={}, productId={}, count={}", rentalId, productId, count);

        // 1. 대여 중인 `status_id` 조회
        List<String> statusIds = userRentalMapper.getRentedStatusIds(productId, rentalId, count);
        log.info("조회된 상태 ID: {}", statusIds);

        if (statusIds == null || statusIds.isEmpty()) {
            throw new RuntimeException("반납 처리 오류: 대여 중인 상태 ID를 찾을 수 없습니다. productId=" + productId);
        }

        // 2. 관리 방식 판단
        boolean isIndividualManaged = statusIds.size() > 1; // 개별 관리 여부 판단
        log.info("관리 방식: {}", isIndividualManaged ? "개별 관리" : "일괄/사이즈별 관리");

        // 3. 관리 방식에 따른 처리
        for (String statusId : statusIds) {
            log.info("처리 중인 Status ID: {}", statusId);

            try {
                // 로그 삭제
                int logDeleted = userRentalMapper.deleteRentalLog(statusId, rentalId);
                if (logDeleted <= 0) {
                    log.error("반납 로그 삭제 실패: statusId={}, rentalId={}", statusId, rentalId);
                    throw new RuntimeException("반납 로그 삭제 실패. Status ID: " + statusId);
                }

                // 상태 복구
                int restored = userRentalMapper.restoreToAvailable(statusId);
                if (restored <= 0) {
                    log.error("상태 복구 실패: statusId={}", statusId);
                    throw new RuntimeException("상태 복구 실패. Status ID: " + statusId);
                }

                log.info("반납 처리 완료: Status ID={}", statusId);
            } catch (Exception e) {
                log.error("반납 처리 중 오류 발생: Status ID={}", statusId, e);
                throw e;
            }
        }

        // 전체 상태 업데이트
        userRentalMapper.updateCurrentStatus(productId);
        log.info("전체 상태 업데이트 완료: productId={}", productId);
    }


    public int updatePaymentStatus(String rental_id, boolean payment_status) {
        Map<String, Object> map = new HashMap<>();
        map.put("rental_id", rental_id);
        map.put("payment_status", payment_status);

        int result = userRentalMapper.updatePaymentStatus(map);
        if (result == 0) {
            throw new RuntimeException("Payment status 업데이트 실패 : ");
        }
        return result;
    }

    // 대여 조회
    public List<RentalDTO> getAllRentals() {
        return userRentalMapper.getAllRentals();
    }

    public RentalDTO getRentalById(String rental_id) {
        return userRentalMapper.getRentalById(rental_id);
    }

    public List<ProductRentalUserDTO> getUserRentalListData(String user_id) {
        return userRentalMapper.getUserRentalListData(user_id);
    }


//    (영준) user_id가 아니라 이메일을 가져오기 위한 코드
    public String getEmailByUserId(String user_id){
        return userRentalMapper.getEmailByUserId(user_id);
    }

    public List<RentalDTO> getOverdueRentals() {
        return userRentalMapper.getOverdueRentals();
    }
}
