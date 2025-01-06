package com.green.smarty.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.green.smarty.vo.AnnounceVO;
import com.green.smarty.vo.AttendanceVO;
import com.green.smarty.vo.BoardVO;
import com.green.smarty.vo.ChatbotVO;
import com.green.smarty.vo.ClassDetailVO;
import com.green.smarty.vo.ClassVO;
import com.green.smarty.vo.CourtVO;
import com.green.smarty.vo.EnrollmentVO;
import com.green.smarty.vo.FacilityAttachVO;
import com.green.smarty.vo.FacilityVO;
import com.green.smarty.vo.LoginHistoryVO;
import com.green.smarty.vo.MembershipVO;
import com.green.smarty.vo.NotificationVO;
import com.green.smarty.vo.PaymentVO;
import com.green.smarty.vo.ProductAttachVO;
import com.green.smarty.vo.ProductStatusVO;
import com.green.smarty.vo.ProductVO;
import com.green.smarty.vo.RentalVO;
import com.green.smarty.vo.ReservationVO;
import com.green.smarty.vo.UserVO;

@Mapper
public interface PublicMapper {
    // VO list
    List<AnnounceVO> getAnnounceAll();

    List<AttendanceVO> getAttendanceAll();

    List<BoardVO> getBoardAll();

    List<ChatbotVO> getChatbotAll();

    List<ClassDetailVO> getClassDetailAll();

    List<ClassVO> getClassAll();

    List<CourtVO> getCourtAll();

    List<EnrollmentVO> getEnrollmentAll();

    List<FacilityAttachVO> getFacilityAttachAll();

    List<FacilityVO> getFacilityAll();

    List<LoginHistoryVO> getLoginHistoryAll();

    List<MembershipVO> getMembershipAll();

    List<NotificationVO> getNotificationAll();

    List<PaymentVO> getPaymentAll();

    List<ProductAttachVO> getProductAttachAll();

    List<ProductStatusVO> getProductStatusAll();

    List<ProductVO> getProductAll();

    List<RentalVO> getRentalAll();

    List<ReservationVO> getReservationAll();

    List<UserVO> getUserAll();

    // VO One
    AnnounceVO getAnnounce(int announce_id);

    AttendanceVO getAttendance(String attendance_id);

    BoardVO getBoard(int board_id);

    ChatbotVO getChatbot(String chat_room);

    ClassDetailVO getClassDetail(String class_id); // 얘는 PK없음.

    ClassVO getClassOne(String class_id); // 얘는 getClass가 내장 메서드여서 이름 살짝 다름

    CourtVO getCourt(String court_id);

    EnrollmentVO getEnrollment(String enrollment_id);

    FacilityAttachVO getFacilityAttach(String facility_id); // 얘도 PK없음

    FacilityVO getFacility(String facility_id);

    LoginHistoryVO getLoginHistory(int id);

    MembershipVO getMembership(String membership_id);

    NotificationVO getNotification(String notification_id);

    PaymentVO getPayment(String payment_id);

    ProductAttachVO getProductAttach(String product_id); // 얘도 PK없음

    ProductStatusVO getProductStatus(String status_id);

    ProductVO getProduct(String product_id);

    RentalVO getRental(String rental_id);

    ReservationVO getReservation(String reservation_id);

    UserVO getUser(String user_id);
}
