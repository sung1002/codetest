package com.wjc.codetest.product.model.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 1. 문제: 데이터 가변성으로 인한 정합성 훼손 및 객체 생성 시 가독성 저하.
 * 2. 원인: @Setter가 열려 있으면 전송 중 데이터가 변조될 수 있으며, 필드가 많아질수록 생성자 파라미터의 순서를 오인하여 잘못된 데이터를 주입할 위험이 있음.
 * 3. 개선안:
 * - @Setter를 제거하고 @Builder를 도입하여 명확하고 안전한 객체 생성 구조 확립.
 * - Jackson 역직렬화를 위한 최소한의 장치로 @NoArgsConstructor(access = AccessLevel.PROTECTED) 설정.
 * - @AllArgsConstructor를 통해 빌더 패턴 작동 기반 마련 및 테스트 코드 편의성 제공.
 * 4. 검증: 빌더를 통해 생성된 객체에 Setter 호출이 불가능함을 확인하여 불변성 증명.
 */
@Getter
@Setter
public class CreateProductRequest {
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

    public CreateProductRequest(String category) {
        this.category = category;
    }

    public CreateProductRequest(String category, String name) {
        this.category = category;
        this.name = name;
    }
}

