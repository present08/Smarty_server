package com.green.smarty.service;

import com.green.smarty.dto.ReplyDTO;
import com.green.smarty.mapper.ReplyMapper;
import com.green.smarty.mapper.UserMapper;
import com.green.smarty.vo.UserVO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@Slf4j
public class ReplyService {
    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private UserMapper userMapper;

    public ReplyDTO insertReply(ReplyDTO replyDTO) {
        // insert 후 영향받은 행의 수를 반환
        UserVO userVO = userMapper.getById(replyDTO.getUser_id());
        if(userVO == null){
            log.error("존재하지 않는 사용자 : {}" , replyDTO.getUser_id());
        }

        int result = replyMapper.insertReply(replyDTO);
        if (result > 0) {
            // 성공적으로 삽입된 경우 replyDTO 반환
            return replyDTO;
        }
        throw new RuntimeException("댓글 작성에 실패했습니다.");
    }

    public List<ReplyDTO> getCommentsByBoardId(int board_id) {
        return replyMapper.getCommentsByBoardId(board_id);
    }

    public void deleteById(int reply_id){
        replyMapper.deleteById(reply_id);
    }

    public int updateById(ReplyDTO replyDTO){
        int result = replyMapper.updateById(replyDTO);
        return result;
    }
}