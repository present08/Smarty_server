package service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.green.smarty.SmartyApplication;
import com.green.smarty.mapper.UserClassMapper;
import com.green.smarty.mapper.UserReservationMapper;
import com.green.smarty.service.UserClassService;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.EnrollmentVO;
import com.green.smarty.vo.FacilityVO;

@SpringBootTest(classes = SmartyApplication.class)
public class Test1 {

    @Autowired
    private UserClassMapper userClassMapper;
    @Autowired
    private UserReservationMapper userReservationMapper;
    @Autowired
    private UserClassService userClassService;

    @Test
    public void dateTest() {
        LocalDate start_date = LocalDate.of(2024, 11, 14);
        LocalDate end_date = LocalDate.of(2024, 11, 30);

        // 시작일~종료일 사이의 월, 수, 금에 해당하는 요일, 날짜 맵에 담기
        List<String> weeekday = new ArrayList<>();
        weeekday.add("월요일");
        weeekday.add("수요일");
        weeekday.add("금요일");

        List<LocalDate> class_date = new ArrayList<>();
        List<String> weekday = new ArrayList<>();

        LocalDate current_date = start_date;
        while (current_date.compareTo(end_date) <= 0) {

            // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
            DayOfWeek current = current_date.getDayOfWeek();
            String currentS = current.getDisplayName(TextStyle.FULL, Locale.getDefault());

            // step2) 지정된 요일과 일치하는 경우 schedule 맵에 담기
            // key: 수업 날짜, value: 수업 요일
            for (String day : weeekday) {
                if (currentS.equals(day))
                    class_date.add(current_date);
                weekday.add(day);
            }
            // step3) 날짜 하루 증가시키기
            current_date = current_date.plusDays(1);
        }
        for (int i = 0; i < class_date.size(); i++) {
            System.out.println(class_date.get(i));
            System.out.println(weekday.get(i));
        }
    }

    @Test
    public void classTest() {
        List<FacilityVO> facility = userReservationMapper.getFacilityAll();
        String facility_id = facility.get(0).getFacility_id();

        LocalDate start_date = LocalDate.of(2024, 11, 16);
        LocalDate end_date = LocalDate.of(2024, 12, 16);

        LocalTime start_time = LocalTime.of(11, 00, 00);
        LocalTime end_time = LocalTime.of(12, 00, 00);

        // 시작일~종료일 사이의 월, 수, 금에 해당하는 요일, 날짜 맵에 담기
        List<String> weeekday = new ArrayList<>();
        weeekday.add("화요일");
        weeekday.add("목요일");
        weeekday.add("금요일");

        List<String> weekdays = new ArrayList<>();
        List<LocalDate> weekDates = new ArrayList<>();

        Map<LocalDate, String> schedule = new HashMap<>();
        LocalDate current_date = start_date;
        while (current_date.compareTo(end_date) <= 0) {

            // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
            DayOfWeek current = current_date.getDayOfWeek();
            String currentS = current.getDisplayName(TextStyle.FULL,
                    Locale.getDefault());

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
                .class_id("C_" + facility_id.substring(12) + "03")
                .facility_id(facility_id)
                .class_name("오늘20241122")
                .start_date(start_date)
                .end_date(end_date)
                .start_time(start_time)
                .end_time(end_time)
                .price(3000)
                .class_size(50)
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
    public void classDetail() {
        List<ClassVO> classList1 = userClassMapper.getClassAll();

        List<String> weeekday = new ArrayList<>();
        weeekday.add("화요일");
        weeekday.add("목요일");
        weeekday.add("금요일");

        for (ClassVO item : classList1) {
            List<String> weekdays = new ArrayList<>();
            List<LocalDate> weekDates = new ArrayList<>();
            LocalDate start_date = item.getStart_date();
            LocalDate end_date = item.getEnd_date();

            Map<LocalDate, String> schedule = new HashMap<>();
            LocalDate current_date = start_date;
            while (current_date.compareTo(end_date) <= 0) {

                // step1) current_date 의 DayOfWeek 객체 생성 및 요일 추출
                DayOfWeek current = current_date.getDayOfWeek();
                String currentS = current.getDisplayName(TextStyle.FULL,
                        Locale.getDefault());

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

            for (int i = 0; i < weekdays.size(); i++) {
                ClassDetailVO detailvo = ClassDetailVO.builder()
                        .class_id(item.getClass_id())
                        .weekday(weekdays.get(i))
                        .class_date(weekDates.get(i))
                        .build();

                userClassMapper.insertClassDetail(detailvo);
            }

        }
    }

    @Test
    public void dateTest1() {
        LocalTime now = LocalTime.now();
        int now1 = Integer.parseInt(now.toString().split(":")[0]);

        LocalDateTime date = LocalDateTime.now();
        LocalDateTime date1 = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), now1, 0);

        System.out.println();
        System.out.println(date1);
        System.out.println();

        LocalDateTime date3 = LocalDateTime.of(2021, 6, 19, 4, 15, 0);
        LocalDateTime date4 = LocalDateTime.of(2021, 6, 19, 6, 20, 30);
    }

    @Test
    public void compareHour() {
        String date = "2024-11-28";
        int default_time = 2;
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);

        String[] dateList = date.split("-");
        for (int i = 0; i < 10; i++) {
            LocalDateTime nowStart = LocalDateTime.of(Integer.parseInt(dateList[0]), Integer.parseInt(dateList[1]),
                    Integer.parseInt(dateList[2]), 5 + i, 0).truncatedTo(ChronoUnit.HOURS);
            System.out.println(nowStart);
            System.out.println(now.compareTo(nowStart.minusHours((long) default_time + 1)));
        }
    }

    @Test
    public void test213() {
        LocalDate now = LocalDate.now();
        System.out.println(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    }

    @Test
    public void classEnrollment() {
        Map<String, String> enrollData = Map.of("user_id","qwe", "class_id","C_642801");
        EnrollmentVO enrollCheck = userClassMapper.enrollCheck(enrollData);
        System.out.println("enrollCheck ==== " + enrollCheck);
    }
}
