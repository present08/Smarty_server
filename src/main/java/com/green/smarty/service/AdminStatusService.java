package com.green.smarty.service;

import com.green.smarty.dto.AdminAttendanceDTO;
import com.green.smarty.dto.AdminEnrollmentDTO;
import com.green.smarty.dto.AdminReservationDTO;
import com.green.smarty.dto.AdminStatusDTO;
import com.green.smarty.mapper.AdminStatusMapper;
import com.green.smarty.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.green.smarty.dto.PermissionDTO;
import com.green.smarty.dto.WidgetDTO;
import com.green.smarty.mapper.PublicMapper;
import com.green.smarty.vo.EnrollmentVO;

@Service
@Transactional
public class AdminStatusService {
    @Autowired
    private AdminStatusMapper adminStatusMapper;
    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private PublicMapper publicMapper;

    // Read
    // 선택한 시설의 예약, 수강 신청 현황
    public AdminStatusDTO getStatus(String facility_id, LocalDate date) {
        // 검색 조건 설정
        Map<String, Object> condition = new HashMap<>();
        condition.put("facility_id", facility_id);
        condition.put("date", date);
        List<AdminReservationDTO> reservationList = adminStatusMapper.getReservation(condition);
        List<AdminEnrollmentDTO> enrollmentList = adminStatusMapper.getEnrollment(condition);
        System.out.println("예약 : " + reservationList);
        System.out.println("수강 : " + enrollmentList);

        AdminStatusDTO adminStatusDTO = AdminStatusDTO.builder()
                .reservationDTOList(reservationList)
                .enrollmentDTOList(enrollmentList)
                .build();       
        return adminStatusDTO;
    }

    public List<AdminAttendanceDTO> getAttendance(String class_id, LocalDate date) {
        // 검색 조건 설정
        Map<String, Object> condition = new HashMap<>();
        condition.put("class_id", class_id);
        condition.put("date", date);
        List<AdminAttendanceDTO> attendanceList = adminStatusMapper.getAttendance(condition);
        System.out.println("출결 : " + attendanceList);
        return attendanceList;
    }

    public List<UserVO> getNewUser(LocalDate date) {
        LocalDate start = date.minusDays(2);
        Map<String, LocalDate> condition = new HashMap<>();
        condition.put("start", start);
        condition.put("date", date);
        List<UserVO> userList = adminStatusMapper.getNewUser(condition);
        for(UserVO userVO : userList) {
            try {
                // QR 코드 생성하여 담기
                byte[] qrCode = qrCodeService.generateQRCode(userVO.getUser_id());
                userVO.setQrCode(qrCode);
            } catch (Exception e) {
                System.out.println("QR 코드 생성 중 오류 발생: " + e.getMessage());
            }
        }
        System.out.println("가입 : " + userList);
        return userList;
    }

    // muam i 77ㅓ
    public List<PermissionDTO> getPermission() {
        List<PermissionDTO> dto = adminStatusMapper.getPermission();
        return dto;
    }

    public void update_enrollment(String enrollment_id) {
        adminStatusMapper.enrollment_update(enrollment_id);
    }

    public void update_enrollment_array(List<String> enrollment_id) {
        List<EnrollmentVO> enrollList = publicMapper.getEnrollmentAll();
        for (EnrollmentVO i : enrollList) {
            for (String j : enrollment_id) {
                if (i.getEnrollment_id().equals(j)) {
                    if (i.getEnrollment_status().equals("승인대기")) {
                        adminStatusMapper.enrollment_update(j);
                    }
                }
            }
        }
    }

    public List<WidgetDTO> getPaymentData() {
        LocalDate today = LocalDate.now();
        Map<String, String> dateData = new HashMap<>();
        String frist_date = today.minusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
        String second_date = today.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
        System.out.println(frist_date);
        System.out.println(second_date);
        dateData.put("frist_date", frist_date);
        dateData.put("second_date", second_date);
        List<WidgetDTO> voList = adminStatusMapper.getPaymentData(dateData);

        return voList;
    }
}
