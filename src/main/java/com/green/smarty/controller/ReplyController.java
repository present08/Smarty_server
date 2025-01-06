package com.green.smarty.controller;

import com.green.smarty.dto.ReplyDTO;
import com.green.smarty.dto.ReservationUserDTO;
import com.green.smarty.mapper.ReplyMapper;
import com.green.smarty.service.ReplyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/comments")
public class ReplyController {
    @Autowired
    private ReplyService replyService;

    @Autowired
    private ReplyMapper replyMapper;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<ReplyDTO> insertReply(@RequestBody ReplyDTO replyDTO) {
        try {
            ReplyDTO savedReply = replyService.insertReply(replyDTO);
            return ResponseEntity.ok(savedReply);
        } catch (Exception e) {
            log.error("댓글 작성 실패:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 댓글 목록 조회
    @GetMapping("/{board_id}")
    public ResponseEntity<List<ReplyDTO>> getComments(@PathVariable int board_id) {
        try {
            List<ReplyDTO> comments = replyService.getCommentsByBoardId(board_id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("댓글 목록 조회 실패:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{reply_id}")
    public void deleteById(@PathVariable int reply_id){
        replyMapper.deleteById(reply_id);
    }

    @PutMapping("/modify/{reply_id}")
    public int updateById(@PathVariable("reply_id") int reply_id , @RequestBody ReplyDTO replyDTO){
        replyDTO.setReply_id(reply_id);
        int update = replyService.updateById(replyDTO);
        return update;
    }
}