package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 1. 문제: 쿼리 파라미터 조작에 따른 데이터 오염 및 잘못된 페이징 범위 요청.
 * 2. 원인: 생성자 없이 Setter로만 바인딩할 경우 객체의 상태가 불안정해지며, page/size 값에 대한 최소한의 방어 로직이 없음.
 * 3. 개선안:
 * - @Builder와 @AllArgsConstructor를 활용하여 생성자 기반 바인딩(Immutable)으로 전환.
 */
@Getter
@Setter
public class GetProductListRequest {
    /**
     * 1. 문제: 필터링 조건의 모호성.
     * 2. 원인: 카테고리가 String이며 필수 여부가 명시되지 않음.
     * 3. 개선안: Enum 타입을 사용하고, 전체 조회를 허용할지 특정 카테고리만 허용할지 비즈니스 정책 명시 필요.
     */
    private String category;

    /**
     * 1. 문제: 잘못된 페이징 값으로 인한 서버 오류 또는 부하 발생 가능성.
     * 2. 원인: page는 0 이상, size는 1 이상이어야 함에도 음수 입력에 대한 방어 로직이 없음.
     * 3. 개선안: @Min 어노테이션을 사용하여 유효 범위를 제한하거나, Setter 내에서 기본값을 설정하는 로직 추가.
     */
    private int page;
    private int size;
}