package com.green.smarty.controller;

import com.green.smarty.service.UserCouponService;
import com.green.smarty.vo.CouponVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/coupons")

public class UserCouponController {

    @Autowired
    private UserCouponService userCouponService;

    // 쿠폰 목록 불러오기
    @GetMapping("/couponList")
    public ResponseEntity<List<CouponVO>> getCouponsByUser(@RequestParam String user_id) {
        System.out.println("유저 아이디 확인: " + user_id);
        List<CouponVO> result = userCouponService.getCouponsByUser(user_id);
        if (result != null && !result.isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
        }
    }

}
