package com.green.smarty.service;

import com.green.smarty.dto.UserClassApplicationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.green.smarty.mapper.UserMembershipMapper;
import com.green.smarty.vo.MembershipVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class UserMembershipService {

    @Autowired
     UserMembershipMapper userMembershipMapper;
//    (영준)
    @Autowired
    private SendEmailService sendEmailService;

    // 결제 금액 합계를 반환하는 메서드
    public float getTotalPaymentAmountByUserId(String userId) {
        float total = userMembershipMapper.getPaymentDetailsByUserId(userId);
        return total;
    }

    public float updateTotalPaymentAmount(String userId) {
        float totalPaymentAmount = userMembershipMapper.getPaymentDetailsByUserId(userId);
        System.out.println("Total 금액 : "+totalPaymentAmount);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("totalPaymentAmount", totalPaymentAmount);
        userMembershipMapper.updateTotalPaymentAmount(map);
        return totalPaymentAmount;
    }

    public boolean saveMembership(MembershipVO membership) {
        return userMembershipMapper.insertMembership(membership) > 0;
    }

    public void updateMembershipLevel(String user_id) {

        // 총 결제 금액 조회
        float totalAmount = userMembershipMapper.getTotalPaymentAmountByUserId(user_id);

        // 금액 비교에 따른 새로운 레벨 결정
        String newLevel = "브론즈"; // 기본 레벨
        if (totalAmount >= 1000) {
            newLevel = "다이아";
        } else if (totalAmount >= 800) {
            newLevel = "플레티넘";
        } else if (totalAmount >= 600) {
            newLevel = "골드";
        } else if (totalAmount >= 300) {
            newLevel = "실버";
        }


        String currentLevel = userMembershipMapper.getlevelbyuserid(user_id);
        String email = userMembershipMapper.getEmailbyuserId(user_id);
        String user_name = userMembershipMapper.getUsernamebyuserId(user_id);

        if(!newLevel.equals(currentLevel)){
            System.out.println("이거 실행되긴함?");
            userMembershipMapper.updateMembershipLevel(user_id, newLevel);
            sendEmailService.sendMembershipLevel(email, newLevel, user_name, user_id);
        }
    }

    public List<MembershipVO> getUserMembergrade (String user_id) {
        List<MembershipVO> result = userMembershipMapper.getUserMembergrade(user_id);
        return result;
    }

//    @Scheduled(cron = "0 0 0 1 1,7 ?") //6월 12월에 수정
//    @Scheduled(cron = "0 */1 * * * ?") // -> 테스트용 1분마다 변경
    public void scheduledMembershipReset() {
        int resetCount = userMembershipMapper.resetMembershipEvery6Months();
        if (resetCount > 0) {
            System.out.println("Memberships reset successfully: " + resetCount);
        } else {
            System.out.println("No memberships required resetting.");
        }
    }
}
