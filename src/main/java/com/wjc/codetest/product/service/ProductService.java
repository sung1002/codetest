package com.wjc.codetest.product.service;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 1. 문제: 데이터 무결성 보장 미흡 및 성능 최적화 누락.
     * 2. 원인: 클래스 또는 메서드 레벨에 @Transactional 선언이 없어 예외 발생 시 롤백되지 않음.
     * 3. 개선안: 클래스 레벨에 @Transactional(readOnly = true)를 설정하고, CUD 작업 메서드에만 @Transactional 명시.
     */
    public Product create(CreateProductRequest dto) {
        Product product = new Product(dto.getCategory(), dto.getName());
        return productRepository.save(product);
    }

    /**
     * 1. 문제: 예외 처리의 모호성 및 가독성 저하
     * - 트랜잭션 동작안함
     * 2. 원인: RuntimeException을 직접 사용하며, isPresent() 체크 후 get()을 호출하는 장황한 방식
     * 3. 개선안: orElseThrow를 사용하여 로직을 간소화하고, 구체적인 예외(Custom Exception 등)로 변경 권장.
     */
    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (!productOptional.isPresent()) {
            throw new RuntimeException("product not found");
        }
        return productOptional.get();
    }

    /**
     * 1. 문제: 불필요한 DB 쓰기 작업 발생.
     * - 트랜잭션 동작 안함
     * 2. 원인: 영속성 컨텍스트의 변경 감지(Dirty Checking)를 활용하지 않고 명시적으로 save()를 호출함.
     * - Transaction Self-Invocation 문제
     * 3. 개선안: @Transactional 안에서 엔티티 상태만 변경하여 트랜잭션 종료 시 자동 업데이트 유도.
     * - product를 가져올때 같은 내부 메서드 getProductById를 사용하는 것이 아닌 productRepository.findById로 변경.
     */
    public Product update(UpdateProductRequest dto) {
        Product product = getProductById(dto.getId());
        product.setCategory(dto.getCategory());
        product.setName(dto.getName());
        Product updatedProduct = productRepository.save(product);
        return updatedProduct;

    }

    /**
     * 1. 문제: 트랜잭션 동작 안함
     * 2. 원인: Transaction Self-Invocation 문제
     * 3. 개선안: product를 가져올때 같은 내부 메서드 getProductById를 사용하는 것이 아닌 productRepository.findById로 변경.
     */
    public void deleteById(Long productId) {
        Product product = getProductById(productId);
        productRepository.delete(product);
    }

    /**
     * 1. 문제: 하드코딩된 정렬 기준 및 페이징 성능 관리 필요.
     * 2. 원인: PageRequest 내부에 정렬 기준이 고정되어 있어 유연성 부족.
     * 3. 개선안: PageRequest 생성 시 Sort 조건을 명시하고 서비스 레이어에서 정렬 전략 관리.
     */
    public Page<Product> getListByCategory(GetProductListRequest dto) {
        PageRequest pageRequest = PageRequest.of(dto.getPage(), dto.getSize(), Sort.by(Sort.Direction.ASC, "category"));
        return productRepository.findAllByCategory(dto.getCategory(), pageRequest);
    }

    public List<String> getUniqueCategories() {
        return productRepository.findDistinctCategories();
    }
}