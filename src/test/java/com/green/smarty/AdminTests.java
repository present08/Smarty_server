package com.green.smarty;

import com.green.smarty.mapper.UserMapper;
import com.green.smarty.service.UserService;
import com.green.smarty.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
public class AdminTests {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertUserTest() {
        for(int i = 1; i < 11; i++) {
            UserVO userVO = UserVO.builder()
                    .user_id("sehyun" + i)
                    .user_name("세현" + i)
                    .email("hyun"+ i + "@naver.com")
                    .password("1234")
                    .phone("01012345678")
                    .address("성남시 분당구 금곡동")
                    .birthday(LocalDate.now())
                    .join_date(LocalDateTime.now())
                    .build();
            userMapper.insertUser(userVO);
            userService.signup(userVO);
        }
        System.out.println("회원 생성 완료");
    }
}
