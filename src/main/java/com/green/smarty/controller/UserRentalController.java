package com.green.smarty.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.dto.EnrollmentClassDTO;
import com.green.smarty.dto.ProductRentalUserDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.dto.UserActivityDTO;
import com.green.smarty.mapper.PaymentMapper;
import com.green.smarty.mapper.PublicMapper;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.mapper.UserProductMapper;
import com.green.smarty.mapper.UserRentalMapper;
import com.green.smarty.service.CartService;
import com.green.smarty.service.SendEmailService;
import com.green.smarty.service.UserRentalService;
import com.green.smarty.vo.RentalVO;
import com.green.smarty.vo.ReservationVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user/rentals")

public class UserRentalController {
    @Autowired
    private UserRentalService userRentalService;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private UserRentalMapper userRentalMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private PublicMapper publicMapper;
    @Autowired
    private PaymentMapper paymentMapper;

    @GetMapping("/")
    public ResponseEntity<List<RentalDTO>> getRental() {
        try {
            List<RentalDTO> rentals = userRentalService.getAllRentals();
            System.out.println("렌탈 리스트: " + rentals);
            return new ResponseEntity<>(rentals, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("렌탈 조회 오류: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> postRental(@RequestBody List<RentalDTO> rentalDTOList) {
        try {
            System.out.println("rentalDTOList 데이터 확인:" + rentalDTOList);
            LocalDateTime date = LocalDateTime.now(); // 현재 날짜 및 시간 가져오기
            List<RentalVO> rentals = new ArrayList<>();

            // 모든 Rental 데이터 가져오기
            List<RentalVO> rentalVOList = publicMapper.getRentalAll();
            List<RentalVO> rentalList = new ArrayList<>();

            for (RentalVO item : rentalVOList) {
                String itemDate = item.getRental_id().substring(2, 10); // ID의 날짜 부분 추출
                String currentDate = date.getYear() +
                        String.format("%02d", date.getMonthValue()) +
                        String.format("%02d", date.getDayOfMonth());

                if (itemDate.equals(currentDate)) {
                    rentalList.add(item);
                }
            }

            for (RentalDTO rentalDTO : rentalDTOList) {
                // 고유 rental_id 생성
                String rentalId = "R_" + date.getYear() +
                        String.format("%02d", date.getMonthValue()) +
                        String.format("%02d", date.getDayOfMonth()) +
                        String.format("%03d", rentalList.size() + 1);
                // (date.getDayOfMonth() < 10 ? String.format("%02d", date.getDayOfMonth()) :
                // date.getDayOfMonth())
                System.out.println(" Rental 생성 확인 ID: " + rentalId);

                // Rental 데이터 생성
                RentalVO rentalVO = RentalVO.builder()
                        .rental_id(rentalId)
                        .user_id(rentalDTO.getUser_id())
                        .product_id(rentalDTO.getProduct_id())
                        .count(rentalDTO.getCount())
                        .rental_date(LocalDateTime.now())
                        .return_date(LocalDateTime.now().plusDays(1)) // 기본 반납일 설정
                        .rental_status(true)
                        .build();

                rentals.add(rentalVO);
                rentalList.add(rentalVO); // 새로 추가된 rental을 리스트에 포함
                System.out.println("RentalVO 이게 맞아? : " + rentalVO);
            }

            userRentalService.insertRentals(rentals);
            System.out.println("Rental 데이터 생성 확인:" + rentals);

            return ResponseEntity.ok("Rental 데이터 삽입 완료");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rental 데이터 삽입 실패: " + e.getMessage());
        }
    }

    // 대여 반납
//    @PutMapping("/{rental_id}/return")
//    public ResponseEntity<String> returnRental(@PathVariable String rental_id, @RequestParam int count) {
//        System.out.println("반납 요청 rental_id: " + rental_id + ", count: " + count);
//        try {
//            int updatedRows = userRentalService.returnRental(rental_id, count);
//            if (updatedRows > 0) {
//                return new ResponseEntity<>("반납 완료", HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>("반납 실패: 해당 렌탈이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
//            }
//        } catch (Exception e) {
//            System.err.println("반납 처리 오류: " + e.getMessage());
//            return new ResponseEntity<>("반납 처리 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PutMapping("/{rental_id}/return")
    public ResponseEntity<RentalDTO> returnRental(@PathVariable String rental_id, @RequestParam int count) {
        try {
            userRentalService.returnRental(rental_id, count);
            RentalDTO updatedRental = userRentalService.getRentalById(rental_id);
            return ResponseEntity.ok(updatedRental);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{rental_id}/payment_status")
    public ResponseEntity<String> updatePaymentStatus(@PathVariable String rental_id,
            @RequestParam boolean payment_status) {
        try {
            int result = userRentalService.updatePaymentStatus(rental_id, payment_status);
            if (result > 0) {
                return ResponseEntity.ok("결제 상태가 성공적으로 업데이트");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("결제 상태 업데이트 실패");
            }
        } catch (Exception e) {
            System.out.println("결제 상태 업데이트 오류" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 상태 업데이트 오류");
        }
    }

    @GetMapping("/list/{user_id}")
    public UserActivityDTO getList(@PathVariable String user_id) {
        List<ReservationVO> reservationVO = publicMapper.getReservationAll();
        List<EnrollmentClassDTO> enrollmentClassDTO = paymentMapper.getEnrollmentClass();
        List<ReservationVO> reservationList = new ArrayList<>();
        List<EnrollmentClassDTO> enrollmentList = new ArrayList<>();

        for (ReservationVO item : reservationVO) {
            if (item.getUser_id().equals(user_id)) {
                reservationList.add(item);
            }
        }
        for (EnrollmentClassDTO item : enrollmentClassDTO) {
            if (item.getUser_id().equals(user_id)) {
                enrollmentList.add(item);
            }
        }
        UserActivityDTO result = UserActivityDTO.builder()
                .enrollmentList(enrollmentList)
                .reservationList(reservationList)
                .build();

        return result;
    }

    @GetMapping("/{rental_id}")
    public ResponseEntity<RentalDTO> getRentalById(@PathVariable String rental_id) {
        System.out.println("특정 대여 조회 렌탈ID: " + rental_id); // 로그 추가
        try {
            RentalDTO rental = userRentalService.getRentalById(rental_id);

            if (rental != null) {
                return new ResponseEntity<>(rental, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/rentalUser")
    public List<ProductRentalUserDTO> getUserRentalListData(@RequestParam String user_id) {
        System.out.println("유저아이디 확인 : " + user_id);
        List<ProductRentalUserDTO> result = userRentalService.getUserRentalListData(user_id);
        return result;
    }

}
