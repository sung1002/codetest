package com.wjc.codetest.product.model.response;

import com.wjc.codetest.product.model.domain.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author : 변영우 byw1666@wjcompass.com
 * @since : 2025-10-27
 */
@Getter
@Setter
public class ProductListResponse {
    /**
     * 1. 문제: 계층 간 분리 원칙(Layer Separation) 위반.
     * 2. 원인: 응답 DTO가 도메인 모델인 'Product' 엔티티에 직접 의존함.
     * 3. 개선안: private List<ProductResponse> products; 와 같이 전용 응답 객체 리스트를 사용.
     * - 클라이언트에게 필요한 정보만 선별하여 전달 가능 (예: id, name, categoryName 등).
     * - 엔티티에 존재하지 않는 계산된 필드를 추가하기 용이함.
     */
    private List<Product> products;
    private int totalPages;
    private long totalElements;
    private int page;

    public ProductListResponse(List<Product> content, int totalPages, long totalElements, int number) {
        this.products = content;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.page = number;
    }
}
