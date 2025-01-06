package com.green.smarty.service;

import com.google.zxing.WriterException;
import com.green.smarty.dto.SecurityResponseDTO;
import com.green.smarty.dto.SecurityUserDTO;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.util.JwtTokenProvider;
import com.green.smarty.vo.CouponVO;
import com.green.smarty.vo.MembershipVO;
import com.green.smarty.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SecurityService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userservice;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private UserMembershipService userMembershipService;

    @Autowired
    private SendEmailService sendEmailService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserCouponService userCouponService;


    private final String SECRET_KEY = "YourSecretKey";
    private final long EXPIRATION_TIME = 3600000; // 1시간

    public ResponseEntity<?> register(UserVO userVO) throws IOException, WriterException {
        // 관리자 계정에만 관리자 권한, 나머지는 사용자 권한
        if(userVO.getUser_id().equals("admin")) userVO.setLevel("admin");
        else userVO.setLevel("user");

        // 가입 날짜와 로그인 날짜 자동 설정
        userVO.setJoin_date(LocalDateTime.now());
        userVO.setLogin_date(LocalDate.now());
        userVO.setUser_status(true);

        // 비밀번호 암호화
        userVO.setPassword(passwordEncoder.encode(userVO.getPassword()));

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
                byte[] qrCode = qrCodeService.generateQRCode(userVO.getUser_id()); // 사용자 이메일을 QR 코드 데이터로 사용
                System.out.println("QR 코드 바이트 배열 길이: " + qrCode.length); // QR 코드 데이터의 길이 로그 출력

                // 영준 추가 이메일 발송 코드
                String emailStatus = sendEmailService.sendWelcomeEmail(userVO.getEmail(),  userVO.getUser_name(), userVO.getUser_id());
                if("FAILURE".equals(emailStatus)){
                    System.out.println("회원가입 성공, 하지만 이메일 전송 중 오류 발생");
                    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);
                }

                // 만약 qr이랑 이메일 발송 전부 성공한다면..
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrCode);  // QR 코드 이미지를 반환

            } catch (Exception e) {
                System.out.println("QR 코드 생성 중 오류 발생: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 성공, 하지만 QR 코드 생성 중 오류가 발생했습니다.");
            }
        } else {
            System.out.println("회원가입 실패");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("UserID or UserEmail already exists.");  // 중복된 사용자 ID 또는 이메일
        }
    }

    public Map<String, Object> login(String userId, String password) {
        // 사용자 조회
        UserVO user = userMapper.findUserById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with user_id: " + userId);
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        SecurityUserDTO securityUserDTO = new SecurityUserDTO(user);

        // JWT 발급
        String token = jwtTokenProvider.createToken(securityUserDTO.getClaims());

        // SecurityResponseDTO 생성
//        return new SecurityResponseDTO(
//                token,
//                user.getUser_name(),
//                user.getUser_id(),
//                user.getLevel()
//        );
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", securityUserDTO);

        return response;
    }

    public Map<String, Object> checkLoginStatus(String token) throws Exception {
        // 토큰 유효성 검증
        Map<String, Object> claims = jwtTokenProvider.validateToken(token);

        // 토큰에서 사용자 ID 추출
        String userId = jwtTokenProvider.getUserId(token);

        // 사용자 정보 조회
        UserVO userVO = userMapper.getById(userId);
        if (userVO == null) {
            throw new Exception("User not found");
        }

        // 응답 데이터 구성
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("isLoggedIn", true);
        userInfo.put("user", userVO);
        return userInfo;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // MyBatis를 통해 사용자 정보 조회
        UserVO user = userMapper.getById(userId);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + userId);
        }

        // SecurityUserDTO로 변환 후 반환
        return new SecurityUserDTO(user);
    }
}
