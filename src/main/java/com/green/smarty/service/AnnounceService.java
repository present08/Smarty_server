package com.green.smarty.service;

import com.green.smarty.dto.AnnounceDTO;
import com.green.smarty.dto.BoardDTO;
import com.green.smarty.mapper.AnnounceMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Log4j2
public class AnnounceService {
    @Autowired
    private AnnounceMapper announceMapper;

    // 공지사항 생성
    public int createAnnounce(AnnounceDTO announceDTO) {
        try {
            int result = announceMapper.insertAnnounce(announceDTO);
            if (result > 0) {
                log.info("Insert successful for announce ID: " + announceDTO.getAnnounce_id());
            } else {
                log.info("Insert failed, no rows affected.");
            }
            return result;
        } catch (Exception e) {
            log.error("Insert failed: ", e);
            return 0;
        }
    }

    // 조건부 검색 조회
    public List<AnnounceDTO> searchAnnounce(String type, String keyword) {
        return announceMapper.searchAnnounce(type, keyword);
    }



    // 공지사항 단일 조회
    public AnnounceDTO getAnnounceById(int announce_id){
        return announceMapper.selectAnnounceById(announce_id);
    }

    // 공지사항 전체 조회
    public List<AnnounceDTO> getAllAnnounce() {
        return announceMapper.selectAllAnnounce();
    }

    // 공지사항 삭제
    public void removeAnnounce(int announce_id){
        announceMapper.removeAnnounce(announce_id);
    }

    // 조회수 증가
    public int incrementViewCount(int announce_id) {
        return announceMapper.updateViewCount(announce_id);
    }

    // 공지사항 수정
    public int modifyAnnounce(AnnounceDTO announceDTO) {
        try{
            AnnounceDTO existingBoard = announceMapper.selectAnnounceById(announceDTO.getAnnounce_id());
            if(existingBoard == null){
                log.error("수정할 게시글이 존재하지 않습니다");
                throw new NoSuchElementException("수정할 게시글이 존재하지 않습니다");
            }

            int result = announceMapper.modifyAnnounce(announceDTO);
            if(result > 0){
                log.info("게시글 수정 성공");
            } else {
                log.warn("게시글 수정 실패");
            }
            return result;
        } catch(Exception e){
            log.error("게시글 수정 중 오류 발생" , e);
            throw e;
        }

    }

}
