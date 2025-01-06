package com.green.smarty.service;

import com.green.smarty.mapper.AdminFacilityMapper;
import com.green.smarty.util.CustomFileUtil;
import com.green.smarty.vo.FacilityAttachVO;
import com.green.smarty.vo.FacilityVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminFacilityService {

    @Autowired
    private CustomFileUtil customFileUtil;

    @Autowired
    private AdminFacilityMapper adminFacilityMapper;

    // 시설 등록 시의 비즈니스 로직 처리
    public String register(FacilityVO facilityVO) throws IOException {

        // 처리1. 시설 id 생성 후 부여
        String id = "FC_" + System.currentTimeMillis();
        facilityVO.setFacility_id(id);
        System.out.println("시설 등록 처리1) id 생성 = " + facilityVO.getFacility_id());

        // 처리2. 첨부파일 유무에 따른 처리
        List<MultipartFile> files = facilityVO.getFiles();

        if (files == null || files.isEmpty() || files.get(0).isEmpty()) {
            System.out.println("시설 등록 처리2-1) 이미지 없음, 기본 이미지 등록");
            Map<String, List<String>> filesInfo = customFileUtil.saveFiles(files);
            adminFacilityMapper.register(facilityVO);

            Map<String, String> filesUpload = new HashMap<>();
            filesUpload.put("facility_id", id);
            filesUpload.put("origin_path", filesInfo.get("origin_path").get(0));
            filesUpload.put("thumbnail_path", filesInfo.get("thumbnail_path").get(0));
            filesUpload.put("file_name", filesInfo.get("file_name").get(0));

            // facility_attach 테이블 등록
            adminFacilityMapper.fileUpload(filesUpload);
        } else {
            System.out.println("시설 등록 처리2-2) 이미지 있음, 파일 하나씩 처리");
            facilityVO.setFacility_images(true);
            // 이미지 여부 true로 변경해주고,
            // facilityDTO의 첨부파일 리스트 기반으로 경로(원본, 썸네일), 파일명 리스트 생성
            Map<String, List<String>> filesInfo = customFileUtil.saveFiles(files);
            facilityVO.setFile_name(filesInfo.get("file_name"));
            adminFacilityMapper.register(facilityVO);

            for (int i = 0; i < files.size(); i++) {
                // 파일 이름 저장
                Map<String, String> filesUpload = new HashMap<>();
                filesUpload.put("facility_id", id);
                filesUpload.put("origin_path", filesInfo.get("origin_path").get(i));
                filesUpload.put("thumbnail_path", filesInfo.get("thumbnail_path").get(i));
                filesUpload.put("file_name", filesInfo.get("file_name").get(i));
                adminFacilityMapper.fileUpload(filesUpload);
            }
        }
        System.out.println("서비스 처리 완료! facilityVO = " + facilityVO);
        return id;
    }

    // 전체 시설 조회
    public List<FacilityVO> getList() {
        // 처리) 각 FacilityVO마다 FacilityAttachVO의 파일 이름 정보 -> FacilityVO.setFile_name
        List<FacilityVO> facilityList = adminFacilityMapper.getList();
        for(FacilityVO facilityVO : facilityList) {
            // 각 FacilityVO의 facility_id 추출하여 파일 정보 얻기
            List<FacilityAttachVO> attachList = adminFacilityMapper.getImages(facilityVO.getFacility_id());
            List<String> file_name = facilityVO.getFile_name(); // facilityVO의 파일명 담을 배열 (비어있음)

            // 각 FacilityVO의 file_name 배열에 FacilityAttachVO 파일 이름 담기
            for(FacilityAttachVO facilityAttachVO : attachList) {
                file_name.add(facilityAttachVO.getFile_name());
            }
            facilityVO.setFile_name(file_name);
            System.out.println("시설 조회 처리) 파일명 저장 file_name = " + file_name);
        }
        return facilityList;
    }

    // 개별 시설 조회
    public FacilityVO read(String facility_id) {
        // 처리) FacilityAttachVO의 파일 이름 정보 -> FacilityVO.setFile_name
        List<FacilityAttachVO> attachList = adminFacilityMapper.getImages(facility_id);
        FacilityVO facilityVO = adminFacilityMapper.read(facility_id);

        List<String> file_name = facilityVO.getFile_name();
        for (FacilityAttachVO facilityAttachVO : attachList) {
            file_name.add(facilityAttachVO.getFile_name());
        }
        facilityVO.setFile_name(file_name);
        return facilityVO;
    }

    public ResponseEntity<Resource> showImages(@PathVariable(name = "file_name") String file_name) {
        System.out.println("파일 조회! file_name = " + file_name);
        return customFileUtil.getFile(file_name);
    }

    public void modify(String facility_id, FacilityVO facilityVO) {
        System.out.println("넘어온 정보 facilityVO : " + facilityVO);
        // 처리1) 기존 시설 정보 불러와서 수정
        List<FacilityAttachVO> originAttachList = adminFacilityMapper.getImages(facility_id);
        FacilityVO originFacilityVO = adminFacilityMapper.read(facility_id);

        List<String> file_name = originFacilityVO.getFile_name();
        for (FacilityAttachVO facilityAttachVO : originAttachList) {
            file_name.add(facilityAttachVO.getFile_name());
        }
        originFacilityVO.setFile_name(file_name);

        originFacilityVO.changeName(facilityVO.getFacility_name());
        originFacilityVO.changeOpenTime(facilityVO.getOpen_time());
        originFacilityVO.changeCloseTime(facilityVO.getClose_time());
        originFacilityVO.changeDefaultTIme(facilityVO.getDefault_time());
        originFacilityVO.changeRate(facilityVO.getRate_adjustment());
        originFacilityVO.changeHotTime(facilityVO.getHot_time());
        originFacilityVO.changeContact(facilityVO.getContact());
        originFacilityVO.changeInfo(facilityVO.getInfo());
        originFacilityVO.changeCaution(facilityVO.getCaution());
        originFacilityVO.changeCourt(facilityVO.isCourt());
        originFacilityVO.changeFacilityStatus(facilityVO.isFacility_status());
        originFacilityVO.changeFacilityImages(facilityVO.isFacility_images());
        System.out.println("시설 수정 처리1) 기존 정보 수정 originFacilityVO = " + originFacilityVO);

        // 처리2) 이미지 수정 여부에 따라 다르게 처리
        if(facilityVO.isFacility_images() && !facilityVO.getFiles().isEmpty()) {
            // 처리2-1) 이미지 수정

            // 기존 이미지 삭제
            adminFacilityMapper.fileRemove(facility_id);
            customFileUtil.deleteFiles(file_name);

            // 새로운 이미지 등록
            List<MultipartFile> files = facilityVO.getFiles();

            System.out.println("시설 수정 처리2-1) 이미지 있음, 파일 하나씩 처리");

            // facilityDTO의 첨부파일 리스트 기반으로 경로(원본, 썸네일), 파일명 리스트 생성
            Map<String, List<String>> filesInfo = customFileUtil.saveFiles(files);
            facilityVO.setFile_name(filesInfo.get("file_name"));

            for (int i = 0; i < files.size(); i++) {
                // 파일 이름 저장
                Map<String, String> filesUpload = new HashMap<>();
                filesUpload.put("facility_id", facility_id);
                filesUpload.put("origin_path", filesInfo.get("origin_path").get(i));
                filesUpload.put("thumbnail_path", filesInfo.get("thumbnail_path").get(i));
                filesUpload.put("file_name", filesInfo.get("file_name").get(i));
                adminFacilityMapper.fileUpload(filesUpload);
            }
        } else if(!facilityVO.isFacility_images() && facilityVO.getFiles().isEmpty()) {

            // 기존 이미지 삭제됨, 기본 이미지 등록
            // 기존 이미지 삭제
            adminFacilityMapper.fileRemove(facility_id);
            customFileUtil.deleteFiles(file_name);

            List<MultipartFile> files = facilityVO.getFiles();

            Map<String, List<String>> filesInfo = customFileUtil.saveFiles(files);

            Map<String, String> filesUpload = new HashMap<>();
            filesUpload.put("facility_id", facility_id);
            filesUpload.put("origin_path", filesInfo.get("origin_path").get(0));
            filesUpload.put("thumbnail_path", filesInfo.get("thumbnail_path").get(0));
            filesUpload.put("file_name", filesInfo.get("file_name").get(0));
            System.out.println("시설 수정 처리2-2) 기존 이미지 삭제, 기본 이미지 등록");
            // facility_attach 테이블 등록
            adminFacilityMapper.fileUpload(filesUpload);
        }
        // 이미지 수정 없음, 기존 시설 정보만 수정
        System.out.println("시설 수정 처리3) 수정 매퍼 전송");
        adminFacilityMapper.modify(originFacilityVO);

    }

    public void remove(String facility_id) {
        adminFacilityMapper.remove(facility_id);
    }
}
