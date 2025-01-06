package com.green.smarty.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.green.smarty.mapper.UserClassMapper;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.EnrollmentVO;

@Service
public class UserClassService {

    @Autowired
    private UserClassMapper userClassMapper;


    public Map<String, List<String>> getClassWeekday() {
        List<ClassDetailVO> detail = userClassMapper.getClassDetail();
        // 데이터를 받아서 중복제거 후
        Map<String, List<String>> result = detail.stream()
                .collect(Collectors.groupingBy(
                        ClassDetailVO::getClass_id,
                        Collectors.mapping(ClassDetailVO::getWeekday, Collectors.toList())));
        // 월요일부터 재배열
        result = result.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().distinct().collect(Collectors.toList())));

        return result;
    }

    public String classEnrollment(Map<String, String> enrollData) {
        List<EnrollmentVO> enroll = userClassMapper.getEnrollment();

        // 수강 정원 계산
        ClassVO classVO = userClassMapper.getClassVO(enrollData.get("class_id"));
        List<EnrollmentVO> enrollSize = userClassMapper.getEnrollSize(enrollData.get("class_id"));
        if (classVO.getClass_size() < enrollSize.size()) {
            return "해당 강의의 정원 초과 되었습니다.";
        }

        // 중복 수강등록 제한
        EnrollmentVO enrollCheck = userClassMapper.enrollCheck(enrollData);
        System.out.println("enrollCheck ==== " + enrollCheck);
        if (enrollCheck != null) {
            return "중복 수강할 수 없습니다.";
        }

        List<EnrollmentVO> enrollList = new ArrayList<>();
        LocalDate date = LocalDate.now();

        for (EnrollmentVO i : enroll) {
            if (i.getEnrollment_id().substring(2, 10).equals(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")))) {
                enrollList.add(i);
            }
        }

        String id = "E_" + date.getYear() + date.getMonthValue()
                + (date.getDayOfMonth() > 9 ? date.getDayOfMonth() : "0" + date.getDayOfMonth())
                + String.format("%03d", enrollList.size() == 0 ? 1 : enrollList.size() + 1);
        enrollData.put("enrollment_id", id);
        EnrollmentVO vo = EnrollmentVO.builder()
                .class_id(enrollData.get("class_id"))
                .enrollment_id(id)
                .user_id(enrollData.get("user_id"))
                .enrollment_status("결제대기")
                .build();
        userClassMapper.classEnroll(vo);
        System.out.println(id);
        return id;
    }
}
