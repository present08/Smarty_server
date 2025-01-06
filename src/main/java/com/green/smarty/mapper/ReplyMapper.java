package com.green.smarty.mapper;

import com.green.smarty.dto.ReplyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReplyMapper {
    // ReplyDTO에서 int로 반환 타입 변경
    int insertReply(ReplyDTO replyDTO);
    List<ReplyDTO> getCommentsByBoardId(int board_id);
    void deleteById(int reply_id);
    int updateById(ReplyDTO replyDTO);
    ReplyDTO selectByReplyId(int reply_id);
}