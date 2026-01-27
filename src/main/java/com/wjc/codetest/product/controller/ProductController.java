package com.wjc.codetest.product.controller;

import com.wjc.codetest.product.model.request.CreateProductRequest;
import com.wjc.codetest.product.model.request.GetProductListRequest;
import com.wjc.codetest.product.model.domain.Product;
import com.wjc.codetest.product.model.request.UpdateProductRequest;
import com.wjc.codetest.product.model.response.ProductListResponse;
import com.wjc.codetest.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 1. 문제: 비표준 API 경로 설계 및 자원 중심적이지 않은 매핑.
 * 2. 원인: RequestMapping에 기본 경로가 없고, 메서드 경로에 행위(get, create, delete)를 포함함.
 * 3. 개선안: RESTful API 설계 원칙에 따라 자원을 명사로 (@RequestMapping("/api/v1/products")), 행위를 HTTP Method(GET, POST, PUT, DELETE)로 표현.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    /**
     * 1. 문제: 엔티티 직접 노출로 인한 보안 및 유연성 저하.
     * 2. 원인: Service에서 반환한 Product 엔티티를 그대로 ResponseEntity에 담아 반환함.
     * 3. 개선안: 도메인 엔티티 대신 전용 응답 DTO(ProductResponse 등)를 사용하여 필요한 필드만 노출하고 내부 구조를 캡슐화함.
     * - 매핑 개선안: @GetMapping("/{productId}")
     */
    @GetMapping(value = "/get/product/by/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "productId") Long productId){
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    /**
     * 1. 문제: 메서드 경로에 행위(get, create, delete)를 포함함, 적합한 HTTP 응답코드가 아님
     * 2. 원인: RESTful API 설계 원칙에 위배함
     * 3. 개선안: ok(200)으로 응답해도 작동에는 문제가 없지만 정확하고 디테일한 응답을 위해서 created(201) 사용을 권장함.
     * - 매핑 개선안: @PostMapping
     */
    @PostMapping(value = "/create/product")
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest dto){
        Product product = productService.create(dto);
        return ResponseEntity.ok(product);
    }

    /**
     * 1. 문제: 부적절한 HTTP 메서드 사용.
     * 2. 원인: 자원 삭제 작업임에도 @PostMapping을 사용하고 경로에 /delete를 명시함.
     * 3. 개선안: @DeleteMapping을 사용하고 경로에서 행위(delete)를 제거하여 REST 규약을 준수.
     * - 매핑 개선안: @DeleteMapping("/{productId}")
     */
    @PostMapping(value = "/delete/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@PathVariable(name = "productId") Long productId){
        productService.deleteById(productId);
        return ResponseEntity.ok(true);
    }

    /**
     * 1. 문제: 멱등성 보장 미흡.
     * 2. 원인: 수정을 위해 @PostMapping을 사용.
     * 3. 개선안: 자원의 전체 수정을 의미하는 @PutMapping 또는 부분 수정을 위한 @PatchMapping 사용 권장.
     * - 매핑 개선안: @PutMapping
     */
    @PostMapping(value = "/update/product")
    public ResponseEntity<Product> updateProduct(@RequestBody UpdateProductRequest dto){
        Product product = productService.update(dto);
        return ResponseEntity.ok(product);
    }

    /**
     * 1. 문제: REST 원칙 위반 및 HTTP 캐싱 활용 불가.
     * 2. 원인: 단순 조회 기능임에도 @PostMapping과 @RequestBody를 사용함.
     * - GET 요청은 멱등성이 보장되어야 하며, 브라우저나 CDN에서 결과를 캐싱할 수 있으나 POST는 이것이 불가능함.
     * - 또한, 검색 조건을 URL(Query Parameter)이 아닌 Body에 숨김으로써 공유(Deep Link)나 북마크가 불가능한 API가 됨.
     * 3. 개선안: @GetMapping으로 변경하고, 검색 조건은 쿼리 파라미터(?category=...&page=0)를 DTO로 바인딩하는 @ModelAttribute 방식을 채택. 어노테이션 생략가능.
     * - 매핑 개선안: @GetMapping("/list")
     */
    @PostMapping(value = "/product/list")
    public ResponseEntity<ProductListResponse> getProductListByCategory(@RequestBody GetProductListRequest dto){
        Page<Product> productList = productService.getListByCategory(dto);

        /**
         * 1. 문제: 도메인 엔티티(Product)의 직접적인 외부 노출.
         * 2. 원인: ProductListResponse 내부의 필드가 List<Product>로 선언되어 있음.
         * - 엔티티의 모든 필드(비공개 내부 데이터 등)가 API로 노출되어 보안에 취약해짐.
         * - 엔티티 구조 변경 시 API 스펙이 강제로 변경되는 강결합(Tight Coupling) 발생.
         * 3. 개선안: ProductListResponse가 List<Product>가 아닌 List<ProductResponse>를 담도록 수정하여
         * 표현 계층(Controller)과 도메인 계층(Entity)을 완전히 분리.
         */
        return ResponseEntity.ok(new ProductListResponse(productList.getContent(), productList.getTotalPages(), productList.getTotalElements(), productList.getNumber()));
    }

    /**
     * 1. 문제: 메서드 이름 중복으로 인한 가독성 및 유지보수 저하.
     * 2. 원인: 상품 목록 조회 메서드와 동일한 이름을 사용하여 코드의 자명성이 떨어짐.
     * 3. 개선안:
     * - 메서드명을 'getUniqueCategories'로 변경하여 "모든 카테고리 종류를 조회한다"는 의도를 명확히 함.
     * - 경로를 /categories로 설정하여 상품(products)의 하위 자원인 카테고리 정보임을 명시.
     * - 매핑 개선안: @GetMapping("/categories")
     */
    @GetMapping(value = "/product/category/list")
    public ResponseEntity<List<String>> getProductListByCategory(){
        List<String> uniqueCategories = productService.getUniqueCategories();
        return ResponseEntity.ok(uniqueCategories);
    }
}