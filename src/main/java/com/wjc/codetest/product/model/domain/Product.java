package com.wjc.codetest.product.model.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
/**
 * 1. 문제: 캡슐화 위반
 * - @Setter가 클래스 레벨에 있어 외부에서 의도치 않게 객체 상태를 변경할 수 있음.
 * - 추후 데이터 변경 시점과 원인을 추적하기 어려움.
 * 2. 원인: Lombok @Setter의 무분별한 사용.
 * 3. 개선안: @Setter 제거. 객체 생성은 생성자로, 변경은 의미 있는 비즈니스 메서드(updateInfo 등)로 제한.
 */
@Setter
/**
 * 1. 문제: DB 예약어 충돌 발생 가능성
 * 2. 원인: 테이블명의 명확한 정의 누락
 * 3. 개선안: @Table(name = "product") 을 통한 명시적 관리
 */
public class Product {

    @Id
    @Column(name = "product_id")
    /**
     * 1. 문제: DB 전략 불일치 및 성능 이슈
     * 2. 원인: MySQL 환경에서 AUTO는 간혹 TABLE 전략(별도 채번 테이블)을 선택하여 성능 저하 유발.
     * 3. 개선안: MySQL(H2 MySQL Mode)에서는 GenerationType.IDENTITY를 명시하는 것이 표준임.
     */
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 1. 문제: 단순 문자열 저장으로 인한 "전자제품", "전자 제품"과 같은 동일 의미의 데이터가 다르게 저장될 가능성.
     * 2. 원인: String 방식의 저장
     * 3. 개선안: 단기적으로는 enum을 활용하여 미리 명확하게 값을 명시하고, 장기적으로 Category를 별도 엔티티로 정규화 하여 관리 필요.
     */
    @Column(name = "category")
    private String category;

    @Column(name = "name")
    private String name;

    /**
     * 1. 문제: JPA 스펙 준수 및 생성자 오남용 방지
     * 2. 원인: JPA는 기본 생성자(NoArgsConstructor)가 필수임. 하지만 public으로 열면 불완전한 객체가 생성될 위험이 있음.
     * 3. 개선안: 현재처럼 protected로 막는 것은 아주 훌륭한 패턴이지만 Lombok 사용 시 @NoArgsConstructor(access = AccessLevel.PROTECTED)가 이 코드를 대신함
     */
    protected Product() {
    }

    public Product(String category, String name) {
        this.category = category;
        this.name = name;
    }

    /**
     * 1. 문제: 불필요한 중복 코드
     * 2. 원인: 클래스 상단 @Getter가 이미 모든 필드의 Getter를 생성함.
     * 3. 개선안: 아래 수동 Getter 메서드들은 삭제하여 가독성 확보.
     */
    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    /**
     * 1. 문제: Entity 동일성 보장 불가
     * - JPA의 영속성 컨텍스트는 데이터베이스에서 가져온 Entity의 식별자가 이미 1차 캐시에 존재하면 해당 Entity를 반환하는 방법으로 영속 상태인 엔티티의 동일성을 보장해 준다.
     * - 그렇기 때문에 실무에서 생략하는 경우도 많음.
     * 2. 원인: 영속성 컨텍스트가 종료된 후(Service 종료 후) 객체를 Set에 담거나 비교할 때, ID가 같아도 다른 객체로 인식됨.
     * 3. 개선안: PK(id) 기반의 equals & hashCode 구현.
     * 4. 주의: Lombok @EqualsAndHashCode는 지양(필드 전체 비교 등 문제 발생). 직접 구현 권장.

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id != null && Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
     */
}
