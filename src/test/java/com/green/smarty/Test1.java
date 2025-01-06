package com.green.smarty;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.green.smarty.mapper.UserClassMapper;
import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.FacilityVO;

@SpringBootTest
public class Test1 {

    @Autowired
    private UserReservationMapper userReservationMapper;

    @Autowired
    private UserClassMapper userClassMapper;

    @Test
    public void dateTest() {
        LocalDate start_date = LocalDate.of(2024, 11, 14);
        LocalDate end_date = LocalDate.of(2024, 11, 30);

        // 시작일~종료일 사이의 월, 수, 금에 해당하는 요일, 날짜 맵에 담기
        List<String> weeekday = new ArrayList<>();
        weeekday.add("월요일");
        weeekday.add("수요일");
        weeekday.add("금요일");

        Map<LocalDate, String> schedule = new HashMap<>();
        LocalDate current_date = start_date;
        while (current_date.compareTo(end_date) <= 0) {

            // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
            DayOfWeek current = current_date.getDayOfWeek();
            String currentS = current.getDisplayName(TextStyle.FULL, Locale.getDefault());

            // step2) 지정된 요일과 일치하는 경우 schedule 맵에 담기
            // key: 수업 날짜, value: 수업 요일
            for (String day : weeekday) {
                if (currentS.equals(day))
                    schedule.put(current_date, currentS);
            }
            // step3) 날짜 하루 증가시키기
            current_date = current_date.plusDays(1);
        }
        System.out.println(schedule);
    }

    @Test
    public void classTest() {
        List<FacilityVO> facility = userReservationMapper.getFacilityAll();
        String facility_id = facility.get(1).getFacility_id();

        LocalDate start_date = LocalDate.of(2024, 12, 23);
        LocalDate end_date = LocalDate.of(2024, 12, 29);

        LocalTime start_time = LocalTime.of(12, 00, 00);
        LocalTime end_time = LocalTime.of(16, 00, 00);

        // 시작일~종료일 사이의 월, 수, 금에 해당하는 요일, 날짜 맵에 담기
        List<String> weeekday = new ArrayList<>();
        weeekday.add("월요일");
        // weeekday.add("화요일");
        // weeekday.add("수요일");
        weeekday.add("목요일");
        weeekday.add("금요일");

        List<String> weekdays = new ArrayList<>();
        List<LocalDate> weekDates = new ArrayList<>();

        Map<LocalDate, String> schedule = new HashMap<>();
        LocalDate current_date = start_date;
        while (current_date.compareTo(end_date) <= 0) {

            // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
            DayOfWeek current = current_date.getDayOfWeek();
            String currentS = current.getDisplayName(TextStyle.FULL, Locale.getDefault());

            // step2) 지정된 요일과 일치하는 경우 schedule 맵에 담기
            // key: 수업 날짜, value: 수업 요일
            for (String day : weeekday) {
                if (currentS.equals(day)) {
                    schedule.put(current_date, currentS);
                    weekdays.add(day);
                    weekDates.add(current_date);
                }
            }
            // step3) 날짜 하루 증가시키기
            current_date = current_date.plusDays(1);
        }

        System.out.println(weekdays);
        System.out.println(weekDates);
        ClassVO classVO = ClassVO.builder()
                .class_id("C_" + facility_id.substring(12) + "05")
                .facility_id(facility_id)
                .class_name("오늘은 내가 박태환")
                .start_date(start_date)
                .end_date(end_date)
                .start_time(start_time)
                .end_time(end_time)
                .price(8000)
                .class_size(20)
                .build();

        userClassMapper.insertClass(classVO);
        // userClassMapper.insertClassDetail(dto.getClass_id());
        for (int i = 0; i < weekdays.size(); i++) {
            ClassDetailVO detailvo = ClassDetailVO.builder()
                    .class_id(classVO.getClass_id())
                    .weekday(weekdays.get(i))
                    .class_date(weekDates.get(i))
                    .build();

            userClassMapper.insertClassDetail(detailvo);
        }
    }

    @Test
    public void classTest2() {
        List<ClassDetailVO> detail = userClassMapper.getClassDetail();
        Map<String, List<String>> result = detail.stream()
                .collect(Collectors.groupingBy(
                        ClassDetailVO::getClass_id,
                        Collectors.mapping(ClassDetailVO::getWeekday, Collectors.toList())));

        result = result.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream().distinct().collect(Collectors.toList())));

        System.out.println(result);
    }

    @Test
    public void paymentData() {

    }
}