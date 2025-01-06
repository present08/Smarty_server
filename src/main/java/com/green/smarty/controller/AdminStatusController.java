package com.green.smarty.controller;

import com.green.smarty.dto.AdminAttendanceDTO;
import com.green.smarty.dto.AdminStatusDTO;
import com.green.smarty.service.AdminStatusService;
import com.green.smarty.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.dto.PermissionDTO;
import com.green.smarty.dto.WidgetDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/status")
public class AdminStatusController {
    @Autowired
    private AdminStatusService adminStatusService;

    // 선택한 시설의 예약, 수강 신청 현황
    @GetMapping("/{facility_id}")
    public AdminStatusDTO getStatus(
            @PathVariable(name = "facility_id") String facility_id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println("시설 이용 현황 조회! date = " + date);
        return adminStatusService.getStatus(facility_id, date);
    }

    // 클래스 출결 현황
    @GetMapping("/attendance/{class_id}")
    public List<AdminAttendanceDTO> getAttendance(
            @PathVariable(name = "class_id") String class_id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println("클래스 출결 조회! date = " + date);
        return adminStatusService.getAttendance(class_id, date);
    }

    // 최근 3일 가입한 회원
    @GetMapping("/user")
    public List<UserVO> getNewUser(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        System.out.println("새로운 회원 조회! date = " + date);
        return adminStatusService.getNewUser(date);
    }

    // muam e 77ㅓ
    @GetMapping("/permission")
    public List<PermissionDTO> getPermission() {
        List<PermissionDTO> dto = adminStatusService.getPermission();
        return dto;
    }

    @PostMapping("/permissionPost")
    public List<PermissionDTO> permissionData(@RequestBody String enrollment_id) {
        adminStatusService.update_enrollment(enrollment_id);
        List<PermissionDTO> dto = adminStatusService.getPermission();
        return dto;
    }

    @PostMapping("/arrpermissionpost")
    public List<PermissionDTO> arrPermission(@RequestBody List<String> enrollment_id) {
        adminStatusService.update_enrollment_array(enrollment_id);
        List<PermissionDTO> dto = adminStatusService.getPermission();
        return dto;
    }

    @GetMapping("/paymentall")
    public List<WidgetDTO> getPaymentData() {
        List<WidgetDTO> vo = adminStatusService.getPaymentData();
        return vo;
    }

}
