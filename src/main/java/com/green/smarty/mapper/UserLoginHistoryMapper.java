package com.green.smarty.mapper;

import com.green.smarty.vo.LoginHistoryVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserLoginHistoryMapper {

    // 로그인 성공 기록 삽입
    void insertLoginHistory(LoginHistoryVO loginHistory);

    // 로그인 실패 기록 삽입
    void insertLoginFailureHistory(LoginHistoryVO loginHistory);

    // 로그아웃 시간 업데이트
    void updateLogoutTime(String userId);


}
