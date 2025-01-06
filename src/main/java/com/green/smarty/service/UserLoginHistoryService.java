package com.green.smarty.service;

import com.green.smarty.mapper.UserLoginHistoryMapper;
import com.green.smarty.vo.LoginHistoryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserLoginHistoryService {

    @Autowired
    private UserLoginHistoryMapper userLoginHistoryMapper;

    // 로그인 성공 기록 삽입
    public void logLoginSuccess(LoginHistoryVO loginHistory) {
        loginHistory.setLogin_status("SUCCESS");
        userLoginHistoryMapper.insertLoginHistory(loginHistory);
    }

    // 로그인 실패 기록 삽입
    public void logLoginFailure(LoginHistoryVO loginHistory) {
        loginHistory.setLogin_status("FAILURE");
        userLoginHistoryMapper.insertLoginFailureHistory(loginHistory);
    }

    // 로그아웃 시간 업데이트
    public void logLogoutTime(String userId) {
        userLoginHistoryMapper.updateLogoutTime(userId);
    }

}
