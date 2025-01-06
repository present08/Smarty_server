package com.green.smarty.controller;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.green.smarty.dto.FacilityDTO;
import com.green.smarty.dto.ReservationDTO;
import com.green.smarty.dto.ReservationUserDTO;
import com.green.smarty.dto.UserReservationDTO;
import com.green.smarty.mapper.PublicMapper;
import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.service.UserReservationService;
import com.green.smarty.vo.AttendanceVO;
import com.green.smarty.vo.PaymentVO;

@RestController
@RequestMapping("/api/user/reservation")
public class UserReservationController {
    @Autowired
    private UserReservationMapper reservationMapper;

    @Autowired
    private UserReservationService reservationService;

    @Autowired
    private PublicMapper publicMapper;

    // 시설 이미지 불러오기
    @GetMapping("/uploads/{fileName}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) throws MalformedURLException {
        Path filePath = Paths.get("upload")
                .resolve(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok().body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 시설 정보 불러오기
    @GetMapping("/")
    public List<FacilityDTO> getFacilityVO() {
        List<FacilityDTO> dto = reservationService.getFacility();
        return dto;
    }

    // Default time 기준으로 버튼 데이터 리턴
    @GetMapping("/{facility_id}")
    public List<Map<String, Integer>> getFacility(@PathVariable String facility_id, @RequestParam String court_id,
            @RequestParam String date) {
        System.out.println("date " + date);
        System.out.println("facility_id " + facility_id);
        System.out.println("court_id " + court_id);
        List<Map<String, Integer>> btnData = reservationService.createTimeBtn(facility_id, court_id, date);
        return btnData;
    }

    // 예약 완료 시 호출
    @PostMapping("/{facility_id}")
    public UserReservationDTO dateToTime(@RequestBody ReservationDTO dto) {
        UserReservationDTO result = reservationService.insertReservation(dto);
        return result;
    }

    // 예약 데이터 삭제
    @DeleteMapping("/{reservation_id}")
    public List<ReservationUserDTO> remove(@PathVariable String reservation_id, @RequestParam String user_id) {
        Map<String, String> paramsMap = new HashMap<>();
        List<PaymentVO> paymentList = publicMapper.getPaymentAll();
        for (PaymentVO i : paymentList) {
            paramsMap.put("reservation_id", reservation_id);
            paramsMap.put("table", "payment");
            if (i.getReservation_id() != null && i.getReservation_id().equals(reservation_id)) {
                reservationMapper.deleteReservationID(paramsMap);
            }
        }
        paramsMap.clear();
        List<AttendanceVO> attendanceList = publicMapper.getAttendanceAll();
        for (AttendanceVO i : attendanceList) {
            paramsMap.put("reservation_id", reservation_id);
            paramsMap.put("table", "attendance");
            if (i.getReservation_id() != null && i.getReservation_id().equals(reservation_id)) {
                reservationMapper.deleteReservationID(paramsMap);
            }
        }

        reservationMapper.deleteReservation(reservation_id);

        List<ReservationUserDTO> result = reservationService.getReservationUserDate(user_id);
        return result;
    }

    @GetMapping("/membership/{user_id}")
    public String getMethodName(@PathVariable String user_id) {
        String membership = reservationMapper.getUserMembership(user_id);
        return membership;
    }

}