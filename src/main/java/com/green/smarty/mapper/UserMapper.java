package com.green.smarty.mapper;

import com.green.smarty.dto.ProductRentalMyPageUserDTO;
import com.green.smarty.dto.UserClassApplicationDTO;
import com.green.smarty.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    // 회원가입 페이지 : 사용자 정보 조회(id, email) , 사용자 추가, QR코드 업데이트
    //사용자 정보 조회 by user_id XML 반환
    UserVO getById (String id);
    // 사용자 정보 조회 by email XML 반환
    UserVO getByEmail (String email);
    // 사용자 추가 (회원가입) XML 반환
    void insertUser (UserVO user);
    // QR 코드로 사용자 업데이트
    int updateUserWithQRCode(UserVO qrCode);

    // 로그인 페이지
    // 로그인한 날짜 업데이트
    void updateLoginDate(@Param("userId") String userId);

    // 아이디 찾기 페이지
    //아이디 찾기 XML 반환
    String findByID (String email, String user_name);

    // 비밀번호 찾기 페이지
    //비밀번호 찾기 XML 반환
    UserVO resetPassword(Map<String, Object> params);

    // 비밀번호 변경 페이지
    //비밀번호 변경
    int updatePassword(UserVO user);

    // 마이페이지 회원정보 수정 (주소, 전화번호)
    // 회원정보 수정
    int updateUserInfo(UserVO userVO);

    // 휴먼상태 확인하는 로직
    // 특정 user_id의 휴면 상태를 확인하는 메서드
    UserVO findUserById(@Param("userId") String userId);
    // 상태 업데이트 쿼리
    void updateUserStatus(@Param("userId") String userId, @Param("userStatus") boolean userStatus);

    // 마이페이지 user_id로 등록한 클래스 정보 가져오기
    //등록한 클래스 정보 가져오기
    List<UserClassApplicationDTO> getClassUserApplication(String user_id);

    // 마이페이지 user_id로 대여한 리스트 정보 가져오기
    // 대여한 리스트 정보 가져오기
    List<ProductRentalMyPageUserDTO> getUserMyPageRentalListData(String user_id);

    // 마이페이지 생일 확인 후 생일 쿠폰 발급
    // 생일 확인
    List<UserVO> getUsersWithBirthday(@Param("month") int month, @Param("day") int day);


    //영준이가 사용중인 로직
    // (영준) 아이디로 이메일 찾기
    String getUserEmailById(@Param("user_id") String user_id);
    // (영준) 아이디로 이름 찾기
    String getUserNameById(String user_id);
    // (영준) 이메일로 아이디 찾기
    String getIdByEmail(String email);
    // (영준) 로그인한지 3개월이 지난 유저 찾기
    List<UserVO> getUserHuman();
    // (영준) 로그인한지 3개월이 지나기 일주일 전 유저 찾기
    List<UserVO> humanSevenbefore();
    // (영준) 이메일 발송을 위해 필요한 정보만 가져오는 Get
    List<UserVO> getUserForSendMail();
}
