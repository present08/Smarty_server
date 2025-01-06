package com.green.smarty.schedule;

import com.green.smarty.dto.NotificationDTO;
import com.green.smarty.dto.RentalDTO;
import com.green.smarty.dto.ReservationDTO;
import com.green.smarty.mapper.*;
import com.green.smarty.service.UserRentalService;
import com.green.smarty.service.UserService;
import com.green.smarty.vo.UserVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.rmi.server.ExportException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class EmailScheduler {

    @Autowired
    private UserRentalService userRentalService;
    @Autowired
    private JavaMailSender javaMailsender;
    @Autowired
    private UserService userService;
    @Autowired
    private UserReservationMapper userReservationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserFacilityMapper userFacilityMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserLoginHistoryMapper userLoginHistoryMapper;
    @Autowired
    private UserRentalMapper userRentalMapper;


    @Scheduled(cron = "0 0 9 1/3 * *") // 매일 아침 9시에 3일에 한번
    public void sendOverdue() {
        List<RentalDTO> overdueRentals = userRentalService.getOverdueRentals();
        for (RentalDTO rentalDTO : overdueRentals) {
            try {
                String email = userRentalService.getEmailByUserId(rentalDTO.getUser_id());
                sendEmail(email, rentalDTO);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(rentalDTO.getUser_id());
                notificationDTO.setStatus("SUCCESS");
                notificationDTO.setUser_name(userMapper.getUserNameById(userMapper.getUserNameById(rentalDTO.getUser_id())));
                notificationDTO.setMessage_type("물품 대여 연체 알림");
                notificationDTO.setResponse_detail("Email sent successfully to " + email);
                notificationMapper.insertByNotificationId(notificationDTO);

                System.out.println("이메일 발송 완료 : " + email);
            } catch (Exception e) {

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(rentalDTO.getUser_id());
                notificationDTO.setStatus("FAILURE");
                notificationDTO.setUser_name(userMapper.getUserNameById(userMapper.getUserNameById(rentalDTO.getUser_id())));
                notificationDTO.setMessage_type("물품 대여 연체 알림");
                notificationMapper.insertByNotificationId(notificationDTO);

                System.out.println("이메일 발송 실패 : {}" + e);
            }
        }
    }

    private void sendEmail(String email, RentalDTO rentalDTO) throws MessagingException {

        String productName = userRentalMapper.getProductNameByProductId(rentalDTO.getProduct_id());
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime rentalDate = rentalDTO.getRental_date();

        long overdueDays = ChronoUnit.DAYS.between(rentalDate, today) - 1;
        overdueDays = Math.max(overdueDays, 0);


        MimeMessage message = javaMailsender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("반납 연체 알림");
        helper.setText("<h1>📢 [알림] 연체된 대여 물품이 있습니다! 📅</h1>"
                + "<p>💼 <strong>상품명:</strong> " + productName + "</p>"
                + "<p>⏳ <strong>연체 일수:</strong> " + overdueDays + "일</p>"
                + "<p>🔔 빠른 반납을 부탁드립니다. 😊</p>"
                + "<p>📞 문의사항이 있으시면 고객센터로 연락 주세요.</p>", true);

        javaMailsender.send(message);
    }

    @Scheduled(cron = "0 0 9 * * *") // 매일 아침 9시에 실행
    public void sendSevendaysBefore() {
        List<ReservationDTO> upcomingOneDays = userReservationMapper.sendOnedaysBefore();
        System.out.println("1분마다 실행은 맞음?");

        for (ReservationDTO reservationDTO : upcomingOneDays) {

            String email = userMapper.getUserEmailById(reservationDTO.getUser_id());
            String facilityName = userFacilityMapper.getFacilityNameById(reservationDTO.getFacility_id());
            String emailContent =
                    "<h1>안녕하세요!</h1>" +
                            "<p>예약하신 일정 하루 전 날 입니다.</p>" +
                            "<p>시설 이름: " + facilityName + "</p>" +
                            "<p>코트 번호: " + reservationDTO.getCourt_id() + "</p>" +
                            "<p>예약 시작: " + reservationDTO.getReservation_start() + "</p>" +
                            "<p>예약 종료: " + reservationDTO.getReservation_end() + "</p>" +
                            "<p>기타 문의 사항이 있으시면 고객센터로 연락 주세요.</p>";
            try {
                // 이메일 내용 생성

                // 이메일 전송
                sendOnedaysBefore(email, emailContent);
                // 알림 정보 생성
                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(reservationDTO.getUser_id());
                notificationDTO.setMessage(emailContent); // 이메일 본문을 메시지로 설정
                notificationDTO.setStatus("SUCCESS");
                notificationDTO.setUser_name(userMapper.getUserNameById(reservationDTO.getUser_id()));
                notificationDTO.setMessage_type("예약 하루 전 알림");
                notificationDTO.setResponse_detail("Email sent successfully to " + email);
                System.out.println(reservationDTO.getUser_id());
                notificationMapper.insertByNotificationId(notificationDTO);

                System.out.println("하루 전 이메일 발송 완료: " + email);
            } catch (Exception e) {

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(reservationDTO.getUser_id());
                notificationDTO.setMessage(emailContent); // 이메일 본문을 메시지로 설정
                notificationDTO.setStatus("FAILURE");
                notificationDTO.setUser_name(userMapper.getUserNameById(reservationDTO.getUser_id()));
                notificationDTO.setMessage_type("예약 하루 전 알림");
                notificationDTO.setResponse_detail("이메일 발송 실패 : {}" + e);
                notificationMapper.insertByNotificationId(notificationDTO);

                System.out.println("하루 전 이메일 발송 실패: " + e.getMessage());
            }
        }
    }

    private void sendOnedaysBefore(String email, String emailContent) throws MessagingException {
        MimeMessage message = javaMailsender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("예약 하루 전 알림");
        helper.setText(emailContent, true); // 이메일 본문 설정
        javaMailsender.send(message);
    }

    @Scheduled(cron = "0 0 9 * * *") // 매일 아침 9시에 실행
    public void sendHumanMessage() {
        List<UserVO> upcomingHuman = userMapper.getUserHuman();
        for (UserVO userVO : upcomingHuman) {
            String email = userVO.getEmail();
            String user_name = userVO.getUser_name();
            String user_id = userVO.getUser_id();
            String email_content =
                    "<h1>📢 안녕하세요, SMARTY 입니다! 💌</h1>" +
                            "<p>회원님의 마지막 로그인으로부터 <strong>3개월</strong>이 지나, 👤 휴먼회원으로 전환되었습니다.</p>" +
                            "<p>👉 계속 서비스를 이용하시려면 <a href='https://smarty-website.com/login'>여기</a>를 클릭하여 로그인해주세요!</p>" +
                            "<br>" +
                            "<p><strong>[SMARTY 계정 정보]</strong></p>" +
                            "<p>회원 이름: " + user_name + "</p>" +
                            "<p>회원 아이디: " + user_id + "</p>" +
                            "<br>" +
                            "<p>📞 기타 문의사항이 있으시면 고객센터로 연락 주세요.</p>" +
                            "<p>SMARTY는 항상 여러분과 함께합니다! 💪</p>" +
                            "<br>" +
                            "<p>SMARTY 팀 드림 🌟</p>";
            try {
                sendHumanMessage(email, email_content);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(user_id);
                notificationDTO.setMessage(email_content);
                notificationDTO.setStatus("SUCCESS");
                notificationDTO.setUser_name(user_name);
                notificationDTO.setMessage_type("휴먼 회원 전환 알림");
                notificationDTO.setResponse_detail("Email sent successfully to :" + email);
                notificationMapper.insertByNotificationId(notificationDTO);
                System.out.println("휴먼 회원 전환 알림 이메일 전송 완료 " + email);
            } catch (Exception e) {

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(user_id);
                notificationDTO.setMessage(email_content);
                notificationDTO.setStatus("FAILURE");
                notificationDTO.setUser_name(user_name);
                notificationDTO.setMessage_type("휴먼 회원 전환 알림");
                notificationDTO.setResponse_detail("이메일 발송 실패 : {} " + e);
                notificationMapper.insertByNotificationId(notificationDTO);

                System.out.println("휴먼 회원 전환 알림 이메일 전송 실패 : " + e.getMessage());
            }
        }
    }

    private void sendHumanMessage(String email, String email_content) throws MessagingException {
        MimeMessage message = javaMailsender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("휴면 회원 전환 알림");
        helper.setText(email_content, true);
        javaMailsender.send(message);
    }


    @Scheduled(cron = "0 0 9 * * *") // 매일 아침 9시에 실행
    public void sendHumanMessageSevendayBefore(){
        System.out.println("스케줄러는 실행중임");
        List<UserVO> sevendaysbeforehuman = userMapper.humanSevenbefore();

        for (UserVO userVO : sevendaysbeforehuman){
            String email = userVO.getEmail();
            String user_name = userVO.getUser_name();
            String user_id = userVO.getUser_id();
            String email_content =
                    "<h1>📢 안녕하세요, SMARTY 입니다! 💌</h1>" +
                            "<p> 회원님 휴먼 회원으로 전환까지 <Strong>일주일</Strong>이 남았습니다. </p>" +
                            "<p>👉 계속 서비스를 이용하시려면 <a href='http://localhost:3000/user/login'>여기</a>를 클릭하여 로그인해주세요!</p>" +
                            "<br>" +
                            "<p>회원 이름: " + user_name + "</p>" +
                            "<p>회원 아이디: " + user_id + "</p>" +
                            "<br>" +
                            "<p>📞 기타 문의사항이 있으시면 고객센터로 연락 주세요.</p>" +
                            "<p>SMARTY는 항상 여러분과 함께합니다! 💪</p>" +
                            "<br>" +
                            "<p>SMARTY 팀 드림 🌟</p>";
            try{
                sendHumanMessageBeforeSevendays(email, email_content);

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(user_id);
                notificationDTO.setMessage(email_content);
                notificationDTO.setStatus("SUCCESS");
                notificationDTO.setUser_name(user_name);
                notificationDTO.setMessage_type("휴먼 회원 전환 일주일 전 알림");
                notificationDTO.setResponse_detail("Email sent successfully to :" + email);
                notificationMapper.insertByNotificationId(notificationDTO);
                System.out.println("휴먼 회원 전환 일주일 전 알림 이메일 전송 완료" + email);

            }catch (Exception e){

                NotificationDTO notificationDTO = new NotificationDTO();
                notificationDTO.setUser_id(user_id);
                notificationDTO.setMessage(email_content);
                notificationDTO.setStatus("FAILURE");
                notificationDTO.setUser_name(user_name);
                notificationDTO.setMessage_type("휴면 회원 전환 일주일 전 알림");
                notificationDTO.setResponse_detail("Email sent successfully to :" + email);
                notificationMapper.insertByNotificationId(notificationDTO);
                System.out.println("휴먼 회원 전환 일주일 전 알림 이메일 전송 실패 : " + email);
            }
        }
    }
    private void sendHumanMessageBeforeSevendays(String email, String email_content) throws  MessagingException{
        MimeMessage message = javaMailsender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("휴먼 회원 전환 일주일 전 알림");
        helper.setText(email_content, true);
        javaMailsender.send(message);
    }
}
