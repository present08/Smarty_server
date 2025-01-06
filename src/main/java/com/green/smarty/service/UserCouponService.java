package com.green.smarty.service;

import com.green.smarty.mapper.UserCouponMapper;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.vo.CouponVO;
import com.green.smarty.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service

public class UserCouponService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserCouponMapper usercouponMapper;

    // 새로운 쿠폰을 사용자에게 추가하는 코드
    // couponVO 쿠폰 정보를 담고 있는 객체
    public void InsertUserNewCoupon(CouponVO couponVO) {
        // 쿠폰 정보를 db에 저장하기 위해 mapper 호출
        usercouponMapper.InsertUserNewCoupon(couponVO);
    }

    // 사용자가 이미 쿠폰을 가지고 있는지 체크하는 코드
    // user_id => 사용자의 ID,
    // 사용자가 이미 쿠폰을 보유하고 있으면 true , 없으면 false 반환
    public boolean CheckExistingCoupon(String user_id) {
        // 해당 사용자가 쿠폰을 보유하고 있는지 확인하는 mapper 호출
        return usercouponMapper.CheckExistingCoupon(user_id);
    }

    // 쿠폰의 상태를 'USED' 상태로 업데이트 하는 코드
    // couponVO 상태 변경할 쿠폰의 정보를 담고 있는 코드
    // 상태 업데이트 성공 시 true, 실패 시 false 반환
    public boolean UserCouponStatus(CouponVO couponVO) {
        // 쿠폰 상태를 'USED'로 변경하기 위해 매퍼 호출
        int result = usercouponMapper.UserCouponStatus(couponVO);
        return result > 0;
    }

    public List<CouponVO> getCouponsByUser(String user_id) {
        return usercouponMapper.getCouponsByUser(user_id);
    }


@Scheduled(cron = "0 0 0 * * ?") // 자정 발급
//@Scheduled(cron = "0 18 * * * ?") // 매 시간 18분에 실행: 테스트용 사용x
public void issueBirthdayCoupons() {

    LocalDate today = LocalDate.now();
    int month = today.getMonthValue();
    int day = today.getDayOfMonth();

    // 생일인 사용자 목록 조회
    List<UserVO> usersWithBirthday = userMapper.getUsersWithBirthday(month, day);

    for (UserVO userVO : usersWithBirthday) {
        System.out.println("User ID: " + userVO.getUser_id() + ", Birthday: " + userVO.getBirthday());
        try {
            // 중복 체크: 이미 발급된 쿠폰이 있는지 확인
            boolean couponExists = usercouponMapper.CheckExistingCoupon(userVO.getUser_id());

            // 중복 체크 결과 출력
            if (couponExists) {
                System.out.println("이미 발급된 쿠폰이 있습니다: " + userVO.getUser_id());
                continue; // 중복이 있을 경우 다음 사용자로 넘어감
            } else {
                System.out.println("쿠폰 발급 가능: " + userVO.getUser_id());
            }

            // 쿠폰 생성
            CouponVO coupon = new CouponVO();
            coupon.setCoupon_id(generateCouponId(userVO)); // 사용자별 쿠폰 ID 생성
            coupon.setUser_id(userVO.getUser_id()); // 해당 사용자 ID 설정
            coupon.setCoupon_name("생일축하 쿠폰"); // 쿠폰 이름 설정
            coupon.setCoupon_code(generateCouponCode()); // 랜덤 쿠폰 코드 생성
            coupon.setStatus("ISSUED"); // 발급 상태 설정
            coupon.setIssue_date(LocalDateTime.now()); // 발급 날짜
            coupon.setExpiry_date(LocalDateTime.now().plusMonths(2)); // 만료 날짜
            coupon.setDiscount_rate(new BigDecimal("10.00")); // 할인율 10%

            // 사용자에게 발급된 쿠폰을 DB에 저장
            usercouponMapper.insertBirthdayCoupon(coupon);
            System.out.println("생일 쿠폰 발급 완료: " + coupon.getUser_id());

        } catch (Exception e) {
            // 발급 실패 시 에러 로그 출력
            System.out.println("생일 쿠폰 발급 실패: " + userVO.getUser_id() + " - " + e.getMessage());
        }
    }
}



    private String generateCouponId(UserVO user) {
        if (user == null || user.getUser_id() == null) {
            throw new IllegalArgumentException("User or User ID cannot be null");
        }
        return "BIRTHDAY_" + user.getUser_id() + "_" + System.currentTimeMillis();
    }

    private String generateCouponCode() {
        return "BD" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

}
