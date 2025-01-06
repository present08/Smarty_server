package com.green.smarty.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.dto.ClassResponseDTO;
import com.green.smarty.dto.UserClassDTO;
import com.green.smarty.mapper.UserClassMapper;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.service.SendEmailService;
import com.green.smarty.service.UserClassService;

@RestController
@RequestMapping("/api/user/class")
public class UserClassController {
    @Autowired
    private UserClassMapper userClassMapper;
    @Autowired
    private UserClassService userClassService;
    @Autowired
    private UserReservationMapper reservationMapper;
    // (영준)
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private UserMapper userMapper;


    @GetMapping("/")
    public ClassResponseDTO getClassAll() {
        List<UserClassDTO> classdto = userClassMapper.getClassDTO();
        Map<String, List<String>> weekday = userClassService.getClassWeekday();
        ClassResponseDTO dto = ClassResponseDTO.builder()
                .weekday(weekday)
                .classDTO(classdto)
                .build();
        return dto;
    }

    @PostMapping("/enroll")
    public String postenollment(@RequestBody Map<String, String> enrollData) {
        String result = userClassService.classEnrollment(enrollData);
        return result;
    }

    @GetMapping("/membership/{user_id}")
    public String getMethodName(@PathVariable String user_id) {
        String membership = reservationMapper.getUserMembership(user_id);
        return membership;
    }

}
