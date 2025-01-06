package com.green.smarty.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResponseDTO<E> {
    // 페이징 처리 결과를 DTO로 캡슐화하여, 클라이언트로 페이지 정보를 전달할 때 필요한 데이터를 계산하고 저장

    private List<E> dtoList; // 페이징된 결과 데이터의 리스트 ex)사용자 리스트
    private List<Integer> pageNumList; // 클라이언트로부터 받은 페이징 요청 정보를 담고 있는 객체
    private PageRequestDTO pageRequestDTO; // 현재 페이징 범위 내의 페이지 번호를 담아 네비게이션에 사용됨
    private boolean prev, next; // 이전, 다음 페이지 존재 여부
    private int totalCount, prevPage, nextPage, totalPage, current;
    // 전체 데이터 개수, 이전 - 다음 페이지 번호, 전체 페이지 수, 현재 페이지 번호

    // lombok의 빌더 패턴 사용하여 객체 생성 시 이 생성자를 호출할 수 있도록 설정
    // withAll이라는 이름의 빌더 패턴 메서드를 커스터마이징
    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(List<E> dtoList, PageRequestDTO pageRequestDTO,
                           long totalCount) {
        // 페이징 정보를 클라이언트로 전달하기 위해 사용 - 페이징과 관련된 다양한 정보를 계산하고 저장

        this.dtoList = dtoList;
        this.pageRequestDTO = pageRequestDTO;
        this.totalCount = (int)totalCount;

        // 현재 페이지를 기준으로 끝 페이지 번호 계산
        int end = (int)(Math.ceil(pageRequestDTO.getPage() / 10.0)) * 10;
        int start = end - 9;

        // 페이지 마지막 번호를 전체 데이터 개수와 페이지당 데이터 수를 기준으로 계산
        int last = (int)(Math.ceil(( totalCount/(double)pageRequestDTO.getSize()) ));
        end = end > last? last : end;   // 계산된 end가 last를 초과하지 않도록 보정

        // start ~ end 까지의 페이지 번호를 리스트로 만듦
        this.pageNumList = IntStream.rangeClosed(start, end)
                .boxed().collect(Collectors.toList());

        // 이전, 다음페이지가 존재하는 경우 prevPage, nextPage 설정
        this.prev = start > 1;
        this.next = totalCount > end * pageRequestDTO.getSize();
        if(prev) this.prevPage = start - 1;
        if(next) this.nextPage = end + 1;

        // 총 페이지 수, 현재 페이지 번호 저장
        this.totalPage = this.pageNumList.size();
        this.current = pageRequestDTO.getPage();
    }
}