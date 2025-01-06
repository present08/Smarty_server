package com.green.smarty.controller;

import com.green.smarty.dto.ProductRentalMyPageUserDTO;
import com.green.smarty.dto.UserClassApplicationDTO;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.service.*;
import com.green.smarty.dto.ReservationUserDTO;
import com.green.smarty.vo.CouponVO;
import com.green.smarty.vo.LoginHistoryVO;
import com.green.smarty.vo.MembershipVO;
import com.green.smarty.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/auth")

public class UserController {

    @Autowired
    private UserService userservice;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private UserReservationService reservationService;

    @Autowired
    private UserMembershipService userMembershipService;

    @Autowired
    private SendEmailService sendEmailService; // 영준 추가 코드

    @Autowired
    private UserLoginHistoryService userLoginHistoryService;

    @Autowired
    private UserCouponService userCouponService;



    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserVO userVO) {

        System.out.println(userVO);

        // 가입 날짜와 로그인 날짜 자동 설정
        userVO.setJoin_date(LocalDateTime.now());
        userVO.setLogin_date(LocalDate.now());
        userVO.setUser_status(true);

        boolean isSuccess = userservice.signup(userVO);

        if (isSuccess) {
            System.out.println("회원가입 성공 : " + userVO);

            try {
                // 멤버십 아이디 발급 및 초기 데이터 저장
                // userId의 앞 두 자리 추출
                String userIdPrefix = userVO.getUser_id().substring(0, Math.min(userVO.getUser_id().length(), 2));
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String membershipId = "M_" + userIdPrefix + timestamp ;

                MembershipVO membership = new MembershipVO();
                membership.setMembership_id(membershipId);
                membership.setMembership_level("브론즈");
                membership.setUser_id(userVO.getUser_id());
                membership.setTotal_payment_amount(0.0f);

                boolean isMembershipSaved = userMembershipService.saveMembership(membership);

                if (!isMembershipSaved) {
                    throw new Exception("멤버십 생성 실패");
                }

                // ** 신규 쿠폰 발급 로직 추가 **
                CouponVO coupon = new CouponVO();

                // 쿠폰 ID 생성
                String couponID = userVO.getUser_id().substring(0, Math.min(userVO.getUser_id().length(), 2));
                String timestamps = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String couponsID = "C_" + couponID + timestamps;

                // 랜덤 쿠폰 코드 생성
                String randomCouponCode = "COUPON_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                coupon.setCoupon_id(couponsID); // 쿠폰 ID
                coupon.setCoupon_code(randomCouponCode); // 랜덤 쿠폰 코드 설정
                coupon.setUser_id(userVO.getUser_id()); // 사용자
                coupon.setCoupon_name("회원가입 축하 쿠폰"); // 무슨 쿠폰인지 ?
                coupon.setStatus("ISSUED"); // 상태
                coupon.setIssue_date(LocalDateTime.now()); // 발급 날짜
                coupon.setExpiry_date(LocalDateTime.now().plusMonths(12)); // 만료 날짜
                coupon.setDiscount_rate(new BigDecimal("10.00")); //할인율 10%

                // 쿠폰 저장
                userCouponService.InsertUserNewCoupon(coupon);
                System.out.println("쿠폰 발급 완료: " + coupon);

                // QR 코드 생성
                byte[] qrCode = qrCodeService.generateQRCode(userVO.getUser_id()); // 사용자 id를 QR 코드 데이터로 사용
                System.out.println("QR 코드 바이트 배열 길이: " + qrCode.length); // QR 코드 데이터의 길이 로그 출력
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);  // QR 코드 이미지를 반환
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 성공, 하지만 QR 코드 생성 중 오류가 발생했습니다.");
            }
        } else {
            System.out.println("회원가입 실패");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("UserID or UserEmail already exists.");  // 중복된 사용자 ID 또는 이메일
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserVO loginRequest, HttpSession session, HttpServletRequest request) {
        // 로그인 요청을 처리
        UserVO user = userservice.login(loginRequest.getUser_id(), loginRequest.getPassword());

        // 로그인 기록 VO 생성
        LoginHistoryVO loginHistory = new LoginHistoryVO();
        loginHistory.setUser_id(loginRequest.getUser_id());
        loginHistory.setIp_address(request.getRemoteAddr());
        loginHistory.setUser_agent(request.getHeader("User-Agent"));

        if (user != null) {
            // 로그인 성공
            userservice.updateLoginDate(user.getUser_id()); // 로그인 날짜 업데이트
            userLoginHistoryService.logLoginSuccess(loginHistory); // 로그인 성공 기록 추가

            session.setAttribute("user", user); // 세션에 사용자 정보 저장

            return ResponseEntity.ok(user); // 로그인 성공 시 사용자 정보 반환
        } else {
            // 로그인 실패
            loginHistory.setLogin_status("FAILURE"); // 실패 상태 설정
            userLoginHistoryService.logLoginFailure(loginHistory); // 로그인 실패 기록 추가

            System.out.println("로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user_id or password.");
        }
    }


    //사용자 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        System.out.println("세션 " + session.getAttribute("JSESSIONID"));
        UserVO userVO = (UserVO) session.getAttribute("user");
        if (userVO != null){
            return ResponseEntity.ok(userVO);
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user.");
        }
    }

    //아이디 찾기
    @PostMapping("/findID")
    public ResponseEntity<?> findID(@RequestBody UserVO vo) {
        String userID = userservice.findByID(vo.getEmail(),vo.getUser_name());
        System.out.println("controller: " + vo);

        if (userID != null) {
            System.out.println("등록된 ID가 있습니다: " + userID);
            return ResponseEntity.ok(Collections.singletonMap("userID", userID));  // 성공 시 ID를 JSON으로 반환
        } else {
            System.out.println("등록된 ID 없습니다");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 이메일로 등록된 아이디가 없습니다.");
        }
    }

    //비밀번호 찾기
    @PostMapping("/find-password")
    public ResponseEntity<?> resetPassword(@RequestBody UserVO vo) {
        // 사용자 정보 확인
        boolean isSuccess = userservice.resetPassword(vo.getUser_id(), vo.getUser_name(), vo.getPhone());

        if (isSuccess) {
            // 성공 시 비밀번호 변경 가능하다는 응답 반환
            return ResponseEntity.ok("비밀번호를 변경할 수 있습니다.");
        } else {
            // 실패 시 404 상태와 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }


    // 비밀번호 변경
    @PostMapping("/pwchange")
    public ResponseEntity<?> updatePassword(@RequestBody UserVO vo) {
        System.out.println(vo.getUser_id());
        System.out.println(vo.getPassword());

        boolean isSuccess = userservice.updatePassword(vo.getUser_id(),vo.getPassword());

        if (isSuccess) {
            // 비밀번호 변경 성공 시 메시지 반환
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            // 비밀번호 변경 실패 시 404 상태와 에러 메시지 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 정보를 찾을 수 없거나 정보가 일치하지 않습니다.");
        }
    }

    // 회원 정보 가져오기 (마이페이지 : 회원정보)
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> checkLoginStatus(HttpServletRequest request) {
        // 세션에서 user 속성 가져오기
        HttpSession session = request.getSession(false);
        if(session != null){
            Boolean isLoggedIn = session.getAttribute("user") != null;
            // 로그인 상태 응답
            Map<String, Boolean> response = new HashMap<>();
            response.put("isLoggedIn", isLoggedIn);
            return ResponseEntity.ok(response);
        }else{
            return null;
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 세션에서 사용자 정보 가져오기
        UserVO user = (UserVO) session.getAttribute("user");

        if (user != null) {
            // 로그아웃 시간 기록
            userLoginHistoryService.logLogoutTime(user.getUser_id());
            System.out.println("로그아웃 기록 추가됨: " + user.getUser_id());
        } else {
            System.out.println("로그아웃 요청 시 세션에 사용자 정보가 없음");
        }

        // 세션 무효화
        session.invalidate();

        System.out.println("로그아웃 성공");
        return ResponseEntity.ok("로그아웃 성공");
    }

    //사용자 휴면 여부 확인
    @GetMapping("/me/{userId}")
    public String getUserStatus(@PathVariable("userId") String userId){
        return userservice.checkUserStatus(userId);
    }

    // qr코드 불러오기
    @GetMapping("/qrcode/{userId}")
    public ResponseEntity<byte[]> getQRCodeForUser(@PathVariable String userId) {
        try {
            byte[] qrCode = qrCodeService.generateQRCode(userId);
            if (qrCode == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("QR 코드가 존재하지 않습니다.".getBytes());
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);  // QR 코드를 반환
        } catch (Exception e) {
            System.out.println("QR 코드 가져오는 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("QR 코드를 가져오는 중 오류가 발생했습니다.".getBytes());
        }
    }

    // 회원정보 수정
    @PutMapping("/info")
    public ResponseEntity<UserVO> updateUserInfo(@RequestBody UserVO userVO) {
        String resultMessage = userservice.updateUserProfile(userVO);
        UserVO user = userMapper.getById(userVO.getUser_id());
        System.out.println("업데이트 완료 :" + user);
            return ResponseEntity.ok(user);
    }

    // 예약정보
    @GetMapping("/reservationUser")
    public List<ReservationUserDTO> getReservationUserDate(@RequestParam String user_id){
        List<ReservationUserDTO> result = reservationService.getReservationUserDate(user_id);
        return result;
    }


     // 수강 리스트 불러오기
    @GetMapping("/classApplication")
    public List<UserClassApplicationDTO> getClassUserApplication(@RequestParam String user_id) {
        List<UserClassApplicationDTO>  result = userservice.getClassUserApplication(user_id);
        return  result;
    }

    // 대여물품 리스트
    @GetMapping("/rentalMyPageUser")
    public List<ProductRentalMyPageUserDTO> getUserMyPageRentalListData(@RequestParam String user_id) {
        List<ProductRentalMyPageUserDTO> result = userservice.getUserMyPageRentalListData(user_id);
        return result;
    }   

    // 로그인 세션 체크
    @GetMapping("/check-session")
    public ResponseEntity<UserVO> checkLogin(HttpServletRequest request) {
        // 세션에서 user 속성 가져오기
        HttpSession session = request.getSession(false);
        if(session != null){
            UserVO userVO = (UserVO) session.getAttribute("user");
            System.out.println("session: "+ userVO);// user이 있다면 로그인 상태로 간주
            return ResponseEntity.ok(userVO);
        }else{
            return null;
        }
    }
}




