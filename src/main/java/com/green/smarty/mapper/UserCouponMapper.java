package com.green.smarty.mapper;

import com.green.smarty.vo.CouponVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserCouponMapper {

    // 회원가입시 쿠폰발급
    void InsertUserNewCoupon (CouponVO couponVO);

    // 중복인지 체크
    boolean CheckExistingCoupon (String user_id);

    // 쿠폰 사용시 상태값 변경
    int UserCouponStatus(CouponVO couponVO);

    //쿠폰목록 불러오기
    List<CouponVO> getCouponsByUser(String user_id);

    // 생일축하 쿠폰 발급
    void insertBirthdayCoupon(CouponVO coupon);

}
