package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 1. 문제: 수정 대상 식별자(ID)의 임의 변경 가능성 및 다중 생성자 관리 비효율.
 * 2. 원인: 수정 요청 DTO에서 ID 값이 Setter로 노출되면 비즈니스 로직 중간에 수정 대상이 바뀔 수 있는 보안 결함 존재.
 * 3. 개선안:
 * - @Setter를 삭제하여 생성 시점의 식별자를 끝까지 보호.
 * - @Builder 패턴을 통해 다양한 필드 업데이트 조합에 유연하게 대응하고 수동 생성자 코드 제거.
 */
@Getter
@Setter
public class UpdateProductRequest {
    /**
     * 1. 문제: 필수 식별자 누락 위험.
     * 2. 원인: 수정 대상의 ID가 null일 경우 서비스 레이어에서 예외가 발생하거나 의도치 않은 동작 수행.
     * 3. 개선안: @NotNull을 통해 API 호출 시 반드시 대상 ID를 포함하도록 강제함.
     */
    private Long id;

    /**
     * 1. 문제: 타입 안정성 부족 및 데이터 파편화.
     * 2. 원인: 카테고리를 String으로 받음으로써 도메인 범위를 벗어난 잘못된 값이나 오타가 입력될 수 있음.
     * 3. 개선안: 앞서 도메인에서 연급한 것과 같이 enum 타입으로 변경하여 허용된 값만 받도록 강제함.
     */
    private String category;

    /**
     * 1. 문제: 빈 값이나 공백 입력 허용.
     * 2. 원인: 별도의 유효성 검사 로직이 없어 이름이 없는 상품이 생성될 수 있음.
     * 3. 개선안: @NotBlank를 사용하여 최소한의 데이터 무결성을 컨트롤러 진입점에서 확보.
     */
    private String name;

    /**
     * 1. 문제: 생성자 오남용 및 가독성 저하.
     * 2. 원인: 인자 개수가 다른 여러 생성자가 섞여 있어 호출부에서 혼동 발생 가능.
     * 3. 개선안: 생성자 대신 빌더(@Builder) 패턴을 사용하거나, 모든 필드를 포함하는 하나의 생성자만 유지하여 일관성 확보.
     */
    public UpdateProductRequest(Long id) {
        this.id = id;
    }

    public UpdateProductRequest(Long id, String category) {
        this.id = id;
        this.category = category;
    }

    public UpdateProductRequest(Long id, String category, String name) {
        this.id = id;
        this.category = category;
        this.name = name;
    }
}

