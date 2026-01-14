package com.wjc.codetest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
/**
 * 1. 문제: 컨트롤러별 예외 처리의 중복 및 일관성 없는 응답 구조.
 * 2. 원인: @ControllerAdvice에 패키지 범위를 한정하여 프로젝트 확장 시 공통 에러 처리가 불가능함.
 * 3. 개선안: 범위를 제거하여 전역 핸들러로 전환하고, @ControllerAdvice 대신 @RestControllerAdvice를 사용.
 * - @RestControllerAdvice = @ControllerAdvice + @ResponseBody 이므로 메서드마다 @ResponseBody를 붙일 필요가 없음.
 */
@ControllerAdvice(value = {"com.wjc.codetest.product.controller"})
public class GlobalExceptionHandler {

    /**
     * 1. 문제: 비정상적인 에러 로깅 및 상태 코드 고정.
     * 2. 원인: 모든 런타임 예외를 무조건 500(Internal Server Error)으로 처리하며, ResponseEntity와 @ResponseStatus가 중복 사용됨.
     * 3. 개선안:
     * - @ResponseStatus를 제거하고 ResponseEntity 하나로 상태 코드를 관리하여 코드의 일관성을 확보.
     * - 로그 출력 시 스택 트레이스(e)를 함께 남겨 정확한 에러 원인 파악이 가능하도록 수정.
     */
    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> runTimeException(Exception e) {
        log.error("status :: {}, errorType :: {}, errorCause :: {}",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "runtimeException",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
