package com.wooshop.common.exception;

public class DuplicateProductCodeException extends RuntimeException {

    public DuplicateProductCodeException(String productCode) {
        super("이미 사용 중인 상품코드입니다 : " + productCode);
    }
}
