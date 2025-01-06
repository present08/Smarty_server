package com.green.smarty.service;

import com.green.smarty.dto.PaymentDetailDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.mapper.*;
import com.green.smarty.vo.CartVO;
import com.green.smarty.vo.PaymentVO;
import com.green.smarty.vo.RentalVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private UserRentalService userRentalService;
    @Autowired
    private PublicMapper publicMapper;
    @Autowired
    private CartMapper cartMapper;
    // (영준) 이메일 발송
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private UserProductMapper userProductMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRentalMapper userRentalMapper;

    public String createPayment(PaymentVO payment) {
        paymentMapper.insertPayment(payment);

        return payment.getPayment_id();
    }


    public RentalVO insertRental(PaymentDetailDTO dto) {
        LocalDateTime date = LocalDateTime.now();
        List<RentalVO> rentalVO = publicMapper.getRentalAll();
        List<RentalVO> rentalList = new ArrayList<>();
        for (RentalVO item : rentalVO) {
            String itemDate = item.getRental_id().substring(2, 10);
            System.out.println(itemDate);
            if(itemDate.equals("" + date.getYear() + date.getMonthValue() + (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()))){
                rentalList.add(item);
            }
        }

        String id = "R_" + date.getYear() + date.getMonthValue()
                + (date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth())
                + String.format("%03d", rentalList.size() + 1);
        System.out.println("rental ID : "+ id);

        //            (영준)
        String user_id = dto.getUser_id();
        String product_id = dto.getProduct_id();
        String userName = userMapper.getUserNameById(user_id);
        String userEmail = userMapper.getUserEmailById(user_id);
        String productName = userProductMapper.getProductNameByProductId(product_id);
        if (userName == null || userEmail == null || productName == null) {
            System.err.println("유효하지 않은 데이터: userName=" + userName + ", userEmail=" + userEmail + ", productName=" + productName);
            throw new IllegalArgumentException("유효하지 않은 데이터입니다.");
        }
        sendEmailService.rentalProduct(userEmail, userName, productName);

        RentalVO vo = RentalVO.builder()
                .rental_id(id)
                .count(dto.getCount())
                .rental_date(date)
                .product_id(dto.getProduct_id())
                .user_id(dto.getUser_id())
                .rental_status(true)
                .build();

        userRentalService.insertRental(vo, dto.getCount());
        System.out.println("insert rental : "+vo);

        return vo;
    }


    // 결제 조회
    public PaymentVO getPaymentById(String payment_id) {
        return paymentMapper.getPaymentById(payment_id);
    }

    // 외부 API로 결제 요청
    public String iportPayment(PaymentVO vo) {
        RestTemplate restTemplate = new RestTemplate();
        String iportApiUrl = "https://api.iamport.kr/payments";
        String accessToken = getIportAccessToken();

        // 요청 본문 생성
        Map<String, Object> requestBody = Map.of(
                "merchant_uid", vo.getPayment_id(),
                "amount", vo.getAmount(),
                "name", "상품명",
                "buyer_name", "구매자 이름");

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 보내기
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(iportApiUrl, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return "결제 요청 성공";
        } else {
            throw new RuntimeException("결제 요청 실패" + response.getBody());
        }
    }

    private String getIportAccessToken() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String tokenApiUrl = "https://api.iamport.kr/users/getToken";

            Map<String, String> requestBody = Map.of(
                    "imp_key", System.getenv("I_PORT_API_KEY"),
                    "imp_secret", System.getenv("I_PORT_API_SECRET"));

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenApiUrl, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, String> tokenData = (Map<String, String>) responseBody.get("response");
                return tokenData.get("access_token");
            } else {
                throw new RuntimeException("액세스 토큰 요청 실패: " + response.getBody());
            }
        } catch (Exception e) {
            throw new RuntimeException("Iamport 토큰 요청 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
