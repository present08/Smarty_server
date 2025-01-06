package com.green.smarty.mapper;

import com.green.smarty.dto.TotalMembershipDTO;
import com.green.smarty.vo.MembershipVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMembershipMapper {


    // 결제 금액의 합계를 반환하는 메서드
    float getPaymentDetailsByUserId(String user_id);

    void updateTotalPaymentAmount(Map<String,Object> map);

    float getTotalPaymentAmountByUserId(String user_id);

    int insertMembership(MembershipVO membership);

    void updateMembershipLevel(@Param("user_id") String user_id, @Param("membership_level") String membership_level);

    List<MembershipVO>getUserMembergrade(String user_id);

    int resetMembershipEvery6Months();


//    (영준) user_id 로 membership_level 찾기
    String getlevelbyuserid(String user_id);
//    (영준) user_id로 email 찾기
    String getEmailbyuserId(String user_id);
//    (영준) user_id로 user_name 찾기
    String getUsernamebyuserId(String user_id);
}