package com.green.smarty.controller;

import com.green.smarty.dto.AnnounceDTO;
import com.green.smarty.dto.BoardDTO;
import com.green.smarty.mapper.AnnounceMapper;
import com.green.smarty.service.AnnounceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/announce")
public class AnnounceController {
    @Autowired
    private AnnounceService announceService;


    //게시글 작성하기
    @PostMapping("/write")
    public ResponseEntity<AnnounceDTO> insertAnnounce(@RequestBody AnnounceDTO announceDTO) {
        try {
            // 디버깅을 위한 로그

            int result = announceService.createAnnounce(announceDTO);
            if (result > 0) {
                AnnounceDTO created = announceService.getAnnounceById(announceDTO.getAnnounce_id());
                return ResponseEntity.ok(created);
            }
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating announcement: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // 공지사항 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<AnnounceDTO>> getAllAnnounce() {
        try {
            List<AnnounceDTO> announcements = announceService.getAllAnnounce();
            System.out.println("조회 했을 때 값은 가져오는지?");
            return ResponseEntity.ok(announcements);
        } catch (Exception e) {
            log.error("Error fetching announcements: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 조건부 검색
    @GetMapping("/search")
    public ResponseEntity<List<AnnounceDTO>> searchAnnounce(
            @RequestParam("type") String type,
            @RequestParam("keyword") String keyword) {
        try {
            List<AnnounceDTO> results = announceService.searchAnnounce(type, keyword);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Error searching announcements: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 조회수 증가 로직
    @PostMapping("/view/{announce_id}")
    public ResponseEntity<Void> incrementViewCount(@PathVariable int announce_id) {
        try {
            announceService.incrementViewCount(announce_id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error incrementing view count: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 공지사항 삭제 로직
    @DeleteMapping("/delete/{announce_id}")
    public void removeAnnounce(@PathVariable int announce_id){
        announceService.removeAnnounce(announce_id);
    }

    // 공지사항 수정 로직
    @PutMapping("/modify/{announce_id}")
    public void modifyAnnounce(@PathVariable int announce_id , @RequestBody AnnounceDTO announceDTO){
        announceDTO.setAnnounce_id(announce_id);
        announceService.modifyAnnounce(announceDTO);
    }

    // 수정을 위한 공지사항 조회
    @GetMapping("/getOneList/{announce_id}")
    public void getAnnounceById(@PathVariable int announce_id){
        System.out.println("여기까지는 들어오는지?");
        announceService.getAnnounceById(announce_id);
    }

}
