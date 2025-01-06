package com.green.smarty.mapper;

import com.green.smarty.dto.AnnounceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AnnounceMapper {
    // 여기서의 반환 타입은 데이터베이스와의 상호작용 후의 값임
    // 대체로 int로 받을지 void 일지는 영향을 미친 행의 갯수가 변했을 때를 기준으로 결정함
    // select는 일반적으로 사용하려고 조회하기 때문에 반환 하는게 일반적임 (List가 일반적이고 단일객체나 맵도 사용)
    // insert도 일반적으로 int형으로 값을 반환하여 성공 유무를 체크함
    // 다른 CRUD 들도 값을 반환하여 성공유무를 판단할 수 있지만 보통은 update 와 delete는 void로 반환함
    int insertAnnounce (AnnounceDTO announceDTO);
    int modifyAnnounce(AnnounceDTO announceDTO);
    void removeAnnounce(int announce_id);
    AnnounceDTO selectAnnounceById(int announce_id);
    // 조회수 증가 메서드
    int updateViewCount(@Param("announce_id") int announce_id);
    // 전체 공지사항 조회
    List<AnnounceDTO> selectAllAnnounce();
    // 조건부 검색 메서드
    List<AnnounceDTO> searchAnnounce(@Param("type") String type, @Param("keyword") String keyword);

}
