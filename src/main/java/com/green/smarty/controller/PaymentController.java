package com.green.smarty.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.green.smarty.dto.*;
import com.green.smarty.mapper.*;
import com.green.smarty.service.*;
import com.green.smarty.vo.RentalVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.mapper.PaymentMapper;
import com.green.smarty.mapper.PublicMapper;
import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.vo.PaymentVO;
import com.green.smarty.vo.ReservationVO;

@RestController
@RequestMapping("/api/user/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private UserMembershipService userMembershipService;

    @Autowired
    private UserReservationService reservationService;

    @Autowired
    private CartService cartService;

    @Autowired
    private PublicMapper publicMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private UserReservationMapper reservationMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRentalMapper userRentalMapper;


    // (영준)
    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private UserFacilityService facilityService;

    @PostMapping("/create")
    public ResponseEntity<String> rentalPayment(@RequestBody PaymentDTO paymentDTO) {
        try {
            LocalDateTime date = LocalDateTime.now();

            List<PaymentVO> paymentVOList = publicMapper.getPaymentAll();
            List<PaymentVO> paymentList = new ArrayList<>();

            for (PaymentVO item : paymentVOList) {
                String itemDate = item.getPayment_id().substring(2, 10); // ID의 날짜 부분 추출
                String currentDate = date.getYear() +
                        String.format("%02d", date.getMonthValue()) +
                        String.format("%02d", date.getDayOfMonth());

                if (itemDate.equals(currentDate)) {
                    paymentList.add(item);
                }
            }

            String paymentId = "P_" + date.getYear() +
                    String.format("%02d", date.getMonthValue()) +
                    String.format("%02d", date.getDayOfMonth()) +
                    String.format("%03d", paymentList.size() + 1);

            PaymentVO payment = PaymentVO.builder()
                    .payment_id(paymentId)
                    .amount(paymentDTO.getAmount())
                    .reservation_id(paymentDTO.getReservation_id())
                    .enrollment_id(paymentDTO.getEnrollment_id())
                    .user_id(paymentDTO.getUser_id())
                    .payment_date(date)
                    .payment_status(true)
                    .build();

            paymentMapper.insertPayment(payment);

            userMembershipService.updateTotalPaymentAmount(paymentDTO.getUser_id());
            userMembershipService.updateMembershipLevel(paymentDTO.getUser_id());

            return ResponseEntity.ok(paymentId);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment 데이터 삽입 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{payment_id}")
    public ResponseEntity<?> getPaymentById(@PathVariable String payment_id) {
        try {
            PaymentVO payment = paymentService.getPaymentById(payment_id);
            if (payment != null) {
                return ResponseEntity.ok(payment);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("결제 데이터를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("결제 조회 중 오류 발생: " + e.getMessage());
        }
    }

    

    // enrollment Payment
    @PostMapping("/enrollment")
    public String enrollPayment(@RequestBody Map<String, String> enrollData) {

        LocalDateTime now = LocalDateTime.now();
        List<PaymentVO> paymentVO = publicMapper.getPaymentAll();
        List<PaymentVO> paymentList = new ArrayList<>();
        for (PaymentVO i : paymentVO) {
            if (i.getPayment_id().substring(2, 10)
                    .equals(now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))) {
                paymentList.add(i);
            }
        }
        String id = "P_" + now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%03d", paymentList.size() + 1);

        PaymentVO vo = PaymentVO.builder()
                .reservation_id(null)
                .enrollment_id(enrollData.get("enrollment_id"))
                .payment_id(id)
                .amount(Float.parseFloat(enrollData.get("amount")))
                .payment_date(now)
                .payment_status(true)
                .build();

        paymentMapper.insertPayment(vo);
        paymentMapper.updateEnroll(enrollData.get("enrollment_id"));

//        // 혜수
//        // 멤버십 업데이트: 총 결제 금액 업데이트
//        userMembershipService.updateTotalPaymentAmount(enrollData.get("user_id"));
//
//        // 멤버십 업데이트(혜수코드)
//        userMembershipService.updateMembershipLevel(
//                enrollData.get("user_id")
//        );

        // (영준) 이메일 발송 코드
        ScatterDTO scatterDTO = paymentMapper.selectScatter(vo.getPayment_id());
        String user_name = userMapper.getUserNameById(scatterDTO.getUser_id());
        String email = userMapper.getUserEmailById(scatterDTO.getUser_id());
        String class_name = scatterDTO.getClass_name();
        System.out.println("User Name: " + user_name);
        System.out.println("Class Name: " + class_name);
        System.out.println("Email: " + email);
        sendEmailService.sendClassReservation(user_name, class_name ,email);


        System.out.println("예약이 완료 됨");
        return "예약 완료";
    }

    // reservation Payment
    @PostMapping("/reservation")
    public UserReservationDTO reserPayment(@RequestBody ReservationDTO dto) {
        // 결제 승인시 reservation Table insert
        reservationMapper.insertReservation(dto);
        
        LocalDateTime now = LocalDateTime.now();
        List<PaymentVO> paymentVO = publicMapper.getPaymentAll();
        List<PaymentVO> paymentList = new ArrayList<>();
        for (PaymentVO i : paymentVO) {
            if (i.getPayment_id().substring(2, 10)
                    .equals(now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")))) {
                paymentList.add(i);
            }
        }
        System.out.println("payment List : " + paymentList);
        String id = "P_" + now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + String.format("%03d", paymentList.size() + 1);
        System.out.println("payment ID : " + id);
        PaymentVO vo = PaymentVO.builder()
                .reservation_id(dto.getReservation_id())
                .enrollment_id(null)
                .payment_id(id)
                .amount(dto.getAmount())
                .payment_date(now)
                .payment_status(true)
                .build();

        // payment Table insert
        paymentMapper.insertPayment(vo);

        // btnData Reset
        UserReservationDTO result = reservationService.insertReservation(dto);

        // 혜수
        // 멤버십 업데이트: 총 결제 금액 업데이트
        userMembershipService.updateTotalPaymentAmount(dto.getUser_id());

        // 멤버십 업데이트: 레벨
        userMembershipService.updateMembershipLevel(dto.getUser_id());

        // (영준) 이메일 발송 관련 코드
        String email = userMapper.getUserEmailById(dto.getUser_id());
        String user_name = userMapper.getUserNameById(dto.getUser_id());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedStart = dto.getReservation_start().format(formatter);
        String formattedEnd = dto.getReservation_end().format(formatter);
        LocalDateTime reservationStart = dto.getReservation_start();
        LocalDateTime reservationEnd = dto.getReservation_end();
        String court_id = dto.getCourt_id();
        String facility_name = facilityService.getFacilityNameById(dto.getFacility_id());
        sendEmailService.sendClassReservation(email,user_name,formattedStart,formattedEnd,facility_name,court_id);

        return result;
    }


}