package com.green.smarty.service;

import com.green.smarty.dto.ClassAdminDTO;
import com.green.smarty.mapper.AdminClassMapper;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.CourtVO;
import com.green.smarty.vo.FacilityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminClassService {
    @Autowired
    private AdminClassMapper adminClassMapper;

    // 강의 등록
    public void register(List<ClassAdminDTO> classList) {

        for(int i = 0; i < classList.size(); i++) {
            if(classList.get(i).getClass_id() == null) {
                // 처리1) class_id 생성 : "C_" + 시설 id 마지막 4자리 + 01
                String facility_id = classList.get(i).getFacility_id();
                String idx = "";
                if( (i+1)-10 < 0 ) idx = "0" + (i+1);
                else idx = "i+1";
                String class_id = "C_" + facility_id.substring(12) + idx;
                classList.get(i).setClass_id(class_id);
                System.out.println("서비스 처리1) 클래스 id 생성 : " + class_id);
            }
            adminClassMapper.register(classList.get(i));

            // 처리2-1) weekday, class_data 생성
            List<String> weekdaySet = classList.get(i).getWeekday();
            System.out.println("서비스 처리2-1) weekdaySet : " + classList.get(i));
            System.out.println("서비스 처리2-1) weekdaySet : " + weekdaySet);

            LocalDate start_date = classList.get(i).getStart_date();
            LocalDate end_date = classList.get(i).getEnd_date();
            LocalDate current_date = start_date;

            List<LocalDate> class_date = new ArrayList<>();
            List<String> weekday = new ArrayList<>();

            while (current_date.compareTo(end_date) <= 0) {

                // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
                DayOfWeek current = current_date.getDayOfWeek();
                String currentS = current.getDisplayName(TextStyle.FULL, Locale.getDefault());

                // step2) 지정된 요일과 일치하는 경우 schedule 맵에 담기
                // key: 수업 날짜, value: 수업 요일
                for (String day : weekdaySet) {
                    if (currentS.equals(day)) {
                        class_date.add(current_date);
                        weekday.add(day);
                    }
                }
                // step3) 날짜 하루 증가시키기
                current_date = current_date.plusDays(1);
            }
            System.out.println("서비스 처리 2-2) 생성된 class_date : " + class_date);
            System.out.println("서비스 처리 2-2) 생성된 weekday : " + weekday);

            // 처리2-2) class_detail 등록
            for(int j = 0; j < class_date.size(); j++) {
                ClassDetailVO classDetailVO = ClassDetailVO.builder()
                        .class_id(classList.get(i).getClass_id())
                        .weekday(weekday.get(j))
                        .class_date(class_date.get(j))
                        .build();
                adminClassMapper.registerDetail(classDetailVO);
            }
        }
    }

    // 선택한 시설 관련 강의 전체 조회
    public List<ClassVO> getList(String facility_id) {
        return adminClassMapper.getList(facility_id);
    }

    // 강의 하나 조회
    public ClassVO read(String class_id) {
        return adminClassMapper.read(class_id);
    }
    // 선택 강의 상세 조회
    public List<ClassDetailVO> getDetailList(String class_id) {
        return adminClassMapper.getDetailList(class_id);
    }

    public void modify(String class_id, ClassAdminDTO classAdminDTO) {
        // 처리1) 비교할 기존의 강의, 상세정보 가져오기
        ClassVO originClassVO = adminClassMapper.read(class_id);
        List<ClassDetailVO> originClassDetailVO = adminClassMapper.getDetailList(class_id);

        originClassVO.changeName(classAdminDTO.getClass_name());
        originClassVO.changeStartDate(classAdminDTO.getStart_date());
        originClassVO.changeEndDate(classAdminDTO.getEnd_date());
        originClassVO.changeStartTime(classAdminDTO.getStart_time());
        originClassVO.changeEndTime(classAdminDTO.getEnd_time());
        originClassVO.changePrice(classAdminDTO.getPrice());
        originClassVO.changeSize(classAdminDTO.getClass_size());
        System.out.println("클래스 수정! originClassVO : " + originClassVO);
        adminClassMapper.modify(originClassVO);

        adminClassMapper.removeDetail(class_id);    // 기존의 detail 모두 삭제
        // 처리2-1) weekday, class_data 생성
        List<String> weekdaySet = classAdminDTO.getWeekday();
        System.out.println("서비스 처리2-1) weekdaySet : " + weekdaySet);

        LocalDate start_date = classAdminDTO.getStart_date();
        LocalDate end_date = classAdminDTO.getEnd_date();
        LocalDate current_date = start_date;

        List<LocalDate> class_date = new ArrayList<>();
        List<String> weekday = new ArrayList<>();

        while (current_date.compareTo(end_date) <= 0) {

            // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
            DayOfWeek current = current_date.getDayOfWeek();
            String currentS = current.getDisplayName(TextStyle.FULL, Locale.getDefault());

            // step2) 지정된 요일과 일치하는 경우 schedule 맵에 담기
            // key: 수업 날짜, value: 수업 요일
            for (String day : weekdaySet) {
                if (currentS.equals(day)) {
                    class_date.add(current_date);
                    weekday.add(day);
                }
            }
            // step3) 날짜 하루 증가시키기
            current_date = current_date.plusDays(1);
        }
        System.out.println("서비스 처리 2-2) 생성된 class_date : " + class_date);
        System.out.println("서비스 처리 2-2) 생성된 weekday : " + weekday);

        // 처리2-2) class_detail 등록
        for(int j = 0; j < class_date.size(); j++) {
            ClassDetailVO classDetailVO = ClassDetailVO.builder()
                    .class_id(classAdminDTO.getClass_id())
                    .weekday(weekday.get(j))
                    .class_date(class_date.get(j))
                    .build();
            adminClassMapper.registerDetail(classDetailVO);
        }

    }

    public void remove(String class_id) {
        adminClassMapper.remove(class_id);
        adminClassMapper.removeDetail(class_id);
    }
}
