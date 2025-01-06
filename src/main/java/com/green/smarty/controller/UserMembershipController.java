package com.green.smarty.controller;

import com.green.smarty.mapper.UserMembershipMapper;
import com.green.smarty.service.UserMembershipService;
import com.green.smarty.vo.MembershipVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/membership")
public class UserMembershipController {

    @Autowired
    UserMembershipService userMembershipService;

    @Autowired
    UserMembershipMapper userMembershipMapper;

    @GetMapping("/totalGrade")
    public float getPaymentDetailsByUserId(@RequestParam String user_id) {
        float totalAmount = userMembershipMapper.getTotalPaymentAmountByUserId(user_id);
        System.out.println("총 결제 금액 : " + totalAmount);
        return totalAmount;
    }

    @GetMapping("/memberGrade")
    public List<MembershipVO> getUserMemberGrade(@RequestParam("user_id") String user_id) {
        List<MembershipVO> result = userMembershipService.getUserMembergrade(user_id);
        return result;
    }


}
