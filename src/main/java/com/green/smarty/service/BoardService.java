package com.green.smarty.service;

import com.green.smarty.dto.BoardDTO;
import com.green.smarty.mapper.BoardMapper;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.vo.UserVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Log4j2
public class BoardService {
    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private UserMapper userMapper;

//    게시글 작성
public int insertBoard(BoardDTO boardDTO) {
    try {
        log.info("받은 게시글 데이터: {}", boardDTO);

        // 필수 필드 검증
        if (boardDTO.getUser_id() == null || boardDTO.getUser_id().trim().isEmpty()) {
            log.error("사용자 ID가 없습니다.");
            throw new IllegalArgumentException("사용자 ID가 필요합니다.");
        }
        if (boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("제목이 필요합니다.");
        }
        if (boardDTO.getContent() == null || boardDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 필요합니다.");
        }
        if (boardDTO.getContent_type() == null || boardDTO.getContent_type().trim().isEmpty()) {
            throw new IllegalArgumentException("카테고리가 필요합니다.");
        }

        // 사용자 존재 여부 확인
        UserVO userVO = userMapper.getById(boardDTO.getUser_id());
        if (userVO == null) {
            log.error("존재하지 않는 사용자: {}", boardDTO.getUser_id());
            throw new NoSuchElementException("존재하지 않는 사용자입니다: " + boardDTO.getUser_id());
        }

        // 기본값 설정
        boardDTO.setView_count(0);
        boardDTO.setGood_btn(0);
        boardDTO.setBad_btn(0);
        boardDTO.setIs_deleted(0);

        int result = boardMapper.insertBoard(boardDTO);
        log.info("게시글 등록 결과: {}", result);
        return result;
    } catch (Exception e) {
        log.error("게시글 등록 중 오류 발생: ", e);
        throw e;
    }
}

//
    public BoardDTO selectBoardById(int board_id){
        BoardDTO boardDTO = boardMapper.selectBoardById(board_id);
        if(boardDTO == null){
            throw new NoSuchElementException("해당하는 아이디에 값이 없음요 : " + board_id);
        }
        return boardDTO;
    }

    // 게시글 삭제
    public void removeBoard(int board_id){
        BoardDTO boardDTO = boardMapper.selectBoardById(board_id);
        if(boardDTO == null){
            throw  new IllegalArgumentException("삭제할 대상이 존재하지 않습니다");
        }
        boardMapper.removeBoard(board_id);
    }
    // 게시글 전체 조회
    public List<BoardDTO> selectAllBoard(){
        return boardMapper.selectAllBoard();
    }

    // 삭제되지 않은 게시글 전체 조회
    public List<BoardDTO> getVisibleBoards() {
        return boardMapper.getVisibleBoards();
    }

    // 조회수 증가
    public int updateViewCount(int board_id) {
        return boardMapper.updateViewCount(board_id);
    }

    // 게시글 수정
    public int updateBoardById(BoardDTO boardDTO) {
        try {
            // 수정 대상 게시글 조회
            BoardDTO existingBoard = boardMapper.selectBoardById(boardDTO.getBoard_id());
            if (existingBoard == null || existingBoard.getIs_deleted() == 1) {
                log.error("수정할 게시글이 존재하지 않거나 이미 삭제되었습니다. board_id: {}", boardDTO.getBoard_id());
                throw new NoSuchElementException("수정할 게시글이 존재하지 않거나 이미 삭제되었습니다. ID: " + boardDTO.getBoard_id());
            }

            // 수정 실행
            int result = boardMapper.updateBoardById(boardDTO);
            if (result > 0) {
                log.info("게시글 수정 성공 - board_id: {}", boardDTO.getBoard_id());
            } else {
                log.warn("게시글 수정 실패 - board_id: {}", boardDTO.getBoard_id());
            }
            return result;
        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생: ", e);
            throw e;
        }
    }


    // 조건부 검색
    public List<BoardDTO> searchBoard(String keyword, String type){
        return boardMapper.searchBoard(keyword, type);
    }

    // 좋아요 증가
    public int updateGood(int board_id){
        return boardMapper.updateGood(board_id);
    }

    // 싫어요 증가
    public int updateBad(int board_id){
        return boardMapper.updateBad(board_id);
    }

    // 게시글 삭제 날짜 저장
    public int deletedDate(int board_id){
        return boardMapper.deletedDate(board_id);
    }

}
