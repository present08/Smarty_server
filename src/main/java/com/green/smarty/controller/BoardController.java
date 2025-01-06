package com.green.smarty.controller;

import com.green.smarty.dto.BoardDTO;
import com.green.smarty.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/board")
public class BoardController {
    @Autowired
    private BoardService boardService;

    @PostMapping("/write")
    public int insertBoard(@RequestBody BoardDTO boardDTO, HttpServletRequest request) {
        return boardService.insertBoard(boardDTO);
    }

    @GetMapping("/list")
    public List<BoardDTO> selectAllBoard() {
        return boardService.selectAllBoard();
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{board_id}")
    public ResponseEntity<Void> removeBoard(@PathVariable int board_id) {
        try {
            boardService.removeBoard(board_id);
            log.info("데이터 삭제 성공");
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error deleting board : ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/detail/{board_id}")
    public ResponseEntity<BoardDTO> selectBoardById(@PathVariable int board_id) {
        try {
            BoardDTO boardDTO = boardService.selectBoardById(board_id);
            return ResponseEntity.ok(boardDTO);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error retrieving board detail:", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 조회수 증가
    @PostMapping("/view/{board_id}")
    public ResponseEntity<Void> updateViewCount(@PathVariable int board_id) {
        try {
            boardService.updateViewCount(board_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error incrementing view count: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 게시글 수정
    @PutMapping("/modify/{board_id}")
    public ResponseEntity<String> updateBoard(
            @PathVariable("board_id") int boardId,
            @RequestBody BoardDTO boardDTO) {
        try {
            log.info("수정 요청 데이터 - board_id: {}, title: {}, content: {}, contentType: {}",
                    boardId, boardDTO.getTitle(), boardDTO.getContent(), boardDTO.getContent_type());

            // 수정 요청 처리
            boardDTO.setBoard_id(boardId); // DTO에 board_id 명시적으로 설정
            int updatedRows = boardService.updateBoardById(boardDTO);

            // 결과 처리
            if (updatedRows > 0) {
                log.info("게시글 수정 성공 - board_id: {}", boardId);
                return ResponseEntity.ok("게시글이 성공적으로 수정되었습니다.");
            } else {
                log.warn("게시글 수정 실패 - board_id: {}", boardId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("수정할 게시글을 찾을 수 없습니다.");
            }
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 데이터: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (NoSuchElementException e) {
            log.error("게시글 수정 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("서버 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    // 삭제되지 않은 게시글 전체 조회
    @GetMapping("/nodeletelist")
    public List<BoardDTO> getVisibleBoards() {
        return boardService.getVisibleBoards();
    }

    // 조건부 검색
    @GetMapping("/search")
    public ResponseEntity<List<BoardDTO>> searchBoard(
            @RequestParam("keyword") String keyword,
            @RequestParam("type") String type){
        try{
            log.info("검색 요청 파라미터 - type: {}, keyword: {}", type, keyword); // 파라미터 로깅
            List<BoardDTO> result = boardService.searchBoard(type, keyword);
            log.info("검색 결과 데이터: {}", result); // 결과 데이터 로깅
            return ResponseEntity.ok(result);
        } catch (Exception e){
            log.error("검색 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 좋아요 증가
    @PostMapping("/view/good/{board_id}")
    public ResponseEntity<Void> updateGood(@PathVariable int board_id) {
        try {
            boardService.updateGood(board_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error incrementing view count: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 싫어요 증가
    @PostMapping("/view/bad/{board_id}")
    public ResponseEntity<Void> updateBad(@PathVariable int board_id) {
        try {
            boardService.updateBad(board_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error incrementing view count: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 삭제 날짜 저장
    @PostMapping("/delete/{board_id}")
    public ResponseEntity<Integer> deletedDate(@PathVariable int board_id){
        boardService.deletedDate(board_id);
        return ResponseEntity.ok().build();
    }





}