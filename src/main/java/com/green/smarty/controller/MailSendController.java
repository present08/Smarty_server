package com.green.smarty.controller;

import com.green.smarty.dto.MailSendDTO;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.service.SendEmailService;
import com.green.smarty.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/mail")
@Log4j2
public class MailSendController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    SendEmailService sendEmailService;

    @GetMapping("/getList")
    public List<UserVO> getUserListForEmail(){
        return userMapper.getUserForSendMail();
    }

    @PostMapping("/sendMail")
    public List<MailSendDTO> mailSend(@RequestBody MailSendDTO mailSendDTO) {
        System.out.println("여기까지는 오는지 확인");
        // 이메일 리스트를 순회하면서 각각 발송
        if (mailSendDTO.getEmail() != null) {
            for (String email : mailSendDTO.getEmail()) {
                sendEmailService.sendHandEmail(email, mailSendDTO.getSubject(), mailSendDTO.getContent());
            }
        }
        return List.of(mailSendDTO);
    }
}
