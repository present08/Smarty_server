package com.green.smarty.service;

import com.green.smarty.mapper.AdminCourtMapper;
import com.green.smarty.mapper.AdminFacilityMapper;
import com.green.smarty.vo.CourtVO;
import com.green.smarty.vo.FacilityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AdminCourtService {
    @Autowired
    private AdminCourtMapper adminCourtMapper;
    @Autowired
    private AdminFacilityMapper adminFacilityMapper;

    // 코트 등록
    public List<String> register(List<CourtVO> courtList) {
        String facility_id = courtList.get(0).getFacility_id();
        List<String> courtIdList = new ArrayList<>();

        for(int i = 0; i < courtList.size(); i++) {
            // 처리) 코트 id 생성하여 하나씩 매퍼로 전달
            //      : "C_" + 시설 id 마지막 4자리 + 01~
            String idx = "";
            if( (i+1)-10 < 0 ) idx = "0" + (i+1);
            else idx = "i+1";
            String court_id = "C_" + facility_id.substring(12) + idx;
            System.out.println("코트 등록 처리) 생성된 코트 id = " + court_id);
            courtList.get(i).setCourt_id(court_id);
            courtIdList.add(court_id);
            adminCourtMapper.register(courtList.get(i));

        }
        return courtIdList;
    }

    public List<CourtVO> getList(String facility_id) {
        return adminCourtMapper.getList(facility_id);
    }

    public CourtVO read(String court_id) {
        return adminCourtMapper.read(court_id);
    }

    public void modify(String facility_id, List<CourtVO> courtList) {
        // 처리1) 비교할 기존의 코트 목록 가져오기
        List<CourtVO> originCourtList = adminCourtMapper.getList(facility_id);
        FacilityVO facilityVO = adminFacilityMapper.read(facility_id);
        String maxCourtIdx = adminCourtMapper.maxCourtIdx(facility_id);

        if(!facilityVO.isCourt() && courtList.get(0).getCourt_id() == null) {
            // 1. 기본 코트만 존재 -> 신규 코트 추가
            // 처리1-1) 기본 코트 삭제, facilityVO의 court 값 변경
            adminCourtMapper.remove(originCourtList.get(0).getCourt_id());
            facilityVO.changeCourt(true);
            adminFacilityMapper.modify(facilityVO);
            // 처리1-2) 추가된 코트 등록
            for(int i = 0; i < courtList.size(); i++) {
                // 처리) 코트 id 생성하여 하나씩 매퍼로 전달
                //      : "C_" + 시설 id 마지막 4자리 + 01~
                String idx = "";
                if( (i+1)-10 < 0 ) idx = "0" + (i+1);
                else idx = i+1 + "";
                String court_id = "C_" + facility_id.substring(12) + idx;
                System.out.println("코트 등록 처리) 생성된 코트 id = " + court_id);
                courtList.get(i).setCourt_id(court_id);
                adminCourtMapper.register(courtList.get(i));
            }
        } else if(facilityVO.isCourt() && courtList.get(0).getCourt_id() == null) {
            // 2. 생성 코트 존재 + 신규 코트 추가
            // 가장 큰 class_id의 끝 2자리 추출 -> 숫자로 변환 -> 1 큰 숫자부터 id 생성
            int maxIdx = Integer.parseInt(maxCourtIdx.substring(6));
            for(int i = 0; i < courtList.size(); i++) {
                String idx = "";
                if( (maxIdx+(i+1))-10 < 0 ) idx = "0" + (maxIdx+(i+1));
                else idx = (maxIdx+(i+1)) + "";
                String court_id = "C_" + facility_id.substring(12) + idx;
                courtList.get(i).setCourt_id(court_id);
                adminCourtMapper.register(courtList.get(i));
            }
        } else if(facilityVO.isCourt() && courtList.get(0).getCourt_id().equals(originCourtList.get(0).getCourt_id())) {
            // 3. 코트 내용만 변경
            for (CourtVO newCourt : courtList) {
                originCourtList.stream().filter(originCourt -> originCourt.getCourt_id().equals(newCourt.getCourt_id()))
                        .forEach(originCourt -> {
                                    originCourt.changeName(newCourt.getCourt_name());
                                    originCourt.changeCourtStatus(newCourt.isCourt_status());
                                    adminCourtMapper.modify(originCourt);
                                    System.out.println("코트 변경 originCourt = " + originCourt);
                                }
                        );
            }
        }
    }

    public void remove(String court_id) {
        adminCourtMapper.remove(court_id);
    }

}
