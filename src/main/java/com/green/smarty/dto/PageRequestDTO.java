package com.green.smarty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
// lombok에서 제공 - getter, setter, toString, equals, hashCode 메서드 자동 생성
@SuperBuilder
// lombok에서 제공 - 빌더 패턴을 사용하여 객체를 생성할 수 있도록 해줌
@AllArgsConstructor
// 모든 필드 초기화 - 객체 생성 시 모든 필드의 값을 직접 설정할 수 있음
@NoArgsConstructor
// 기본 생성자 자동 생성
public class PageRequestDTO {
    // 페이징 처리를 위한 요청 데이터를 담기 위해 사용
    // 현재 페이지 번호, 페이지당 항목 수 -> 사용자가 값을 제공하지 않았을 때에도 기본적으로 동작

    // 빌더 패턴으로 객체 생성 시 기본값 설정 및 유지를 위해 사용
    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 15;
}
