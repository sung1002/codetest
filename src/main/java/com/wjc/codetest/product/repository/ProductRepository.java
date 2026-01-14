package com.wjc.codetest.product.repository;

import com.wjc.codetest.product.model.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 1. 문제: 메서드 시그니처와 파라미터 네이밍의 불일치로 인한 가독성 저하.
     * 2. 원인: category를 조회 조건으로 받으면서 변수명을 'name'으로 지정하여 오해의 소지 있음.
     * 3. 개선안: 파라미터 명을 'category'로 변경하여 의미를 명확히 함.
     */
    Page<Product> findAllByCategory(String name, Pageable pageable);

    /**
     * 1. 문제: 데이터량 증가 시 검색 및 중복 제거 쿼리의 성능 저하
     * 2. 원인: category 필드에 대한 인덱스 부재 시 Full Table Scan 발생.
     * 3. 개선안:
     * - 단기: DB 레벨에서 category 컬럼에 인덱스를 추가.
     * - 장기: Category를 별도 엔티티로 정규화(Normalization)하여 Product와 연관 관계를 맺고,
     * 해당 메서드를 CategoryRepository로 이관하여 도메인 책임을 명확히 함.
     */
    @Query("SELECT DISTINCT p.category FROM Product p")
    List<String> findDistinctCategories();
}
