package com.green.smarty.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
@RequiredArgsConstructor
public class CustomFileUtil{
    // 파일 데이터 입출력 담당

    @Value("upload")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File tempFolder = new File(uploadPath);
        if(tempFolder.exists() == false) tempFolder.mkdir();
        uploadPath = tempFolder.getAbsolutePath();  // 절대 경로로 변환하고 로깅으로 경로 정보 출력
        System.out.println("업로드 파일 절대경로 = "+ uploadPath);
    }

    // 파일 업로드 작업 + 저장된 파일 이름과 경로(원본, 썸네일) 반환
    public Map<String, List<String>> saveFiles(List<MultipartFile> files) throws RuntimeException {

        List<String> file_name = new ArrayList<>();             // 파일명 리스트
        List<String> origin_path = new ArrayList<>();           // 원본 경로 리스트
        List<String> thumbnail_path = new ArrayList<>();        // 썸네일 경로 리스트
        Map<String, List<String>> result = new HashMap<>();     // 최종적으로 반환할 맵

        if(files == null || files.isEmpty()) {
            // 첨부 파일이 없는 경우 기본 이미지가 저장되도록 처리
            try {
                // 기본 이미지 파일 경로 설정
                Path defaultImagePath = Paths.get(new ClassPathResource("images/smarty.jpeg").getURI());
                // 저장할 파일 이름을 생성하고, Paths.get()으로 저장 경로 지정
                String savedName = UUID.randomUUID().toString() + "_default.jpeg";
                Path savePath = Paths.get(uploadPath, savedName);
                // 기본 이미지를 지정한 경로에 복사
                Files.copy(defaultImagePath, savePath);

                // 기본 이미지 썸네일 생성
                Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                Thumbnails.of(savePath.toFile())
                        .size(200, 200)
                        .toFile(thumbnailPath.toFile());

                file_name.add(savedName);
                origin_path.add(savePath.toString());
                thumbnail_path.add(thumbnailPath.toString());
            } catch (IOException e) {
                throw new RuntimeException("기본 이미지 저장 실패", e);
            }
        }

        // 첨부 파일이 있는 경우 하나씩 저장
        for(MultipartFile multipartFile : files) {
            String savedName = UUID.randomUUID().toString() + "_" + multipartFile.getOriginalFilename();
            Path savePath = Paths.get(uploadPath, savedName);
            // 저장할 파일 이름을 생성하고, Paths.get()으로 저장 경로 지정
            origin_path.add(savePath.toString());    // 원본 경로 리스트 저장

            try{
                // Files.copy() 메서드로 실제 파일 데이터를 해당 경로에 복사
                Files.copy(multipartFile.getInputStream(), savePath);
                // 이미지 파일이라면 썸네일 생성
                String contentType = multipartFile.getContentType();
                if(contentType != null && contentType.startsWith("image")) {
                    Path thumbnailPath = Paths.get(uploadPath, "s_" + savedName);
                    Thumbnails.of(savePath.toFile())
                            .size(200, 200)
                            .toFile(thumbnailPath.toFile());
                    thumbnail_path.add(thumbnailPath.toString());    // 썸네일 경로 리스트 저장
                }
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
            file_name.add(savedName);     // 파일 이름 리스트 저장
        }
        result.put("file_name", file_name);
        result.put("origin_path", origin_path);
        result.put("thumbnail_path", thumbnail_path);
        return result;
        //각각의 리스트를 키와 함께 Map에 담아 반환
    }

    // 파일 데이터를 읽어서 스프링에서 제공하는 Resource 타입으로 반환, 특정 파일 조회시 사용
    public ResponseEntity<Resource> getFile (String fileName) {

        Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
        if (!resource.isReadable()) {
            resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
        }

        HttpHeaders headers = new HttpHeaders();

        try {
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
            // 파일의 종류마다 다르게 HTTP 헤더 'Content-Type' 값을 생성해야 하므로\
            // Files.probeContentType()으로 헤더 메세지 생성
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    // 파일 이름을 기준으로 한 번에 여러 개의 파일을 삭제하는 기능
    public void deleteFiles(List<String> fileNames) {

        if(fileNames == null || fileNames.size() == 0) {
            return;
        }

        fileNames.forEach(fileName -> {
            String thumbnailFileName = "s_" + fileName;
            Path thumbnailPath = Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath, fileName);
            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(thumbnailPath);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

}
