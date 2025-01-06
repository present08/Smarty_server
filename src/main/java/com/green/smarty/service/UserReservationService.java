package com.green.smarty.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.green.smarty.mapper.UserMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.green.smarty.dto.FacilityDTO;
import com.green.smarty.dto.ReservationDTO;
import com.green.smarty.dto.ReservationUserDTO;
import com.green.smarty.dto.UserReservationDTO;
import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.vo.FacilityAttachVO;
import com.green.smarty.vo.FacilityVO;
import com.green.smarty.vo.ReservationVO;

@Service
public class UserReservationService {
    @Autowired
    private UserReservationMapper reservationMapper;

    //    (영준)
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserFacilityService userFacilityService;


    public List<FacilityDTO> getFacility() {
        List<FacilityDTO> dto = reservationMapper.getFacilityOFCourt();
        for (FacilityDTO i : dto) {
            List<FacilityAttachVO> f_img = reservationMapper.getFacilityImg(i.getFacility_id());
            List<String> imgList = new ArrayList<>();
            for (FacilityAttachVO j : f_img) {
                imgList.add(j.getFile_name());
            }
            i.setFile_name(imgList);
        }
        return dto;
    }

    // open, close, default time을 기준으로 버튼 생성 및
    // reservation Table에 데이터가 있으면 버튼 비활성화로 변경
    public List<Map<String, Integer>> createTimeBtn(String facility_id, String court_id, String date) {
        System.out.println("=========== Service: Create Btn ==============");
        System.out.println("facility_id  " + facility_id);
        System.out.println("court_id  " + court_id);
        System.out.println("date  " + date);
        Map<String, String> getReservation = new HashMap<>();
        getReservation.put("facility_id", facility_id);
        getReservation.put("court_id", court_id);

        FacilityVO f_vo = reservationMapper.getFacility(facility_id);
        List<ReservationDTO> r_vo = reservationMapper.getReservation(getReservation);

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        int start = Integer.parseInt(f_vo.getOpen_time().split(":")[0]);
        int end = Integer.parseInt(f_vo.getClose_time().split(":")[0]);
        int default_time = f_vo.getDefault_time();
        int cnt = 0;
        int prevTime = 0;

        // reservation 등록되어있는 시간 제외
        List<Integer> reservation_list = new ArrayList<>();
        for (ReservationDTO vo : r_vo) {
            if (vo.getReservation_start().toLocalDate().toString().equals(date)) {
                int start1 = Integer.parseInt(vo.getReservation_start().toLocalTime().toString().split(":")[0]);
                int end1 = Integer.parseInt(vo.getReservation_end().toLocalTime().toString().split(":")[0]);
                for (int i = 0; i < end1 - start1; i++) {
                    reservation_list.add(start1 + i);
                }
            }
        }
        System.out.println("reservationList " + reservation_list);
        String[] dateList = date.split("-");

        // timeLine Btn 신규 버튼 생성
        List<Map<String, Integer>> timeBtn = new ArrayList<>();
        for (int i = 0; i < (end - start); i++) {
            LocalDateTime nowStart = LocalDateTime.of(Integer.parseInt(dateList[0]), Integer.parseInt(dateList[1]),
                    Integer.parseInt(dateList[2]), start + i, 0).truncatedTo(ChronoUnit.HOURS);
            Map<String, Integer> timeMap = new HashMap<>();
            timeMap.put("start", start + i);
            timeMap.put("end", start + i + 1);
            timeMap.put("id", cnt);
            timeMap.put("active", 0);

            if ((end - start) % default_time > (end - start) - i - 1 || reservation_list.contains(start + i))
                timeMap.put("disabled", 1);
            else timeMap.put("disabled", 0);

            timeBtn.add(timeMap);

            if (now.compareTo(nowStart) == 0) prevTime = cnt;
            if ((i + 1) % default_time == 0) cnt++;
        }
        // 현재시간 이전 Btn disabled
        if (now.toLocalDate().compareTo(LocalDate.of(Integer.parseInt(dateList[0]), Integer.parseInt(dateList[1]),
                Integer.parseInt(dateList[2]))) == 0) {
            for (Map<String, Integer> i : timeBtn) {
                if (prevTime >= i.get("id"))
                    i.put("disabled", 1);
            }
        }
        return timeBtn;
    }

    public UserReservationDTO insertReservation(ReservationDTO dto) {
        List<ReservationVO> selectVO = reservationMapper.getReservationAll();
        String date = dto.getReservation_start().toString().split("T")[0];
        
        // 연속예약 제한
        LocalDateTime endTime = dto.getReservation_end().truncatedTo(ChronoUnit.HOURS);
        LocalDateTime startTime = dto.getReservation_start().truncatedTo(ChronoUnit.HOURS);
        int iterable = 0;
        String user_id = dto.getUser_id();
        for (ReservationVO i : selectVO) {
            LocalDateTime reservation_end = i.getReservation_end().truncatedTo(ChronoUnit.HOURS);
            LocalDateTime reservation_start = i.getReservation_start().truncatedTo(ChronoUnit.HOURS);
            
            if (dto.getCourt_id().equals(i.getCourt_id()) && (reservation_end.compareTo(startTime) == 0 || reservation_start.compareTo(endTime) == 0)
                    && i.getUser_id().equals(user_id)) {
                iterable = 1;
            }
        }

        if (iterable == 0) {
            // 마지막 예약번호 받아오기(배열에 데이터가 없을 경우 예외처리)
            String nowDate = "" + LocalDate.now().getYear() + LocalDate.now().getMonthValue()
                    + (LocalDate.now().getDayOfMonth() < 10 ? String.format("%02d", LocalDate.now().getDayOfMonth())
                    : LocalDate.now().getDayOfMonth());
            int last_id = 0;
            System.out.println(nowDate);

            try {
                System.out.println(selectVO.get(selectVO.size() - 1).getReservation_id().substring(2, 10));
                if (selectVO.get(selectVO.size() - 1).getReservation_id().substring(2, 10).equals(nowDate)) {
                    last_id = Integer.parseInt(selectVO.get(selectVO.size() - 1).getReservation_id().substring(10));
                } else {
                    last_id = 0;
                }
            } catch (Exception e) {
                last_id = 0;
            }
            dto.setReservation_id("R_" + nowDate + String.format("%03d", last_id + 1));
            // reservationMapper.insertReservation(dto);

        }

        List<Map<String, Integer>> btnData = createTimeBtn(dto.getFacility_id(), dto.getCourt_id(), date);
        UserReservationDTO resultDTO = UserReservationDTO.builder().btnData(btnData).iterable(iterable)
                .reservation_id(dto.getReservation_id()).build();

        return resultDTO;
    }

    public List<ReservationUserDTO> getReservationUserDate(String user_id) {
        List<ReservationUserDTO> result = reservationMapper.getReservationUserDate(user_id);
        return result;
    }
}