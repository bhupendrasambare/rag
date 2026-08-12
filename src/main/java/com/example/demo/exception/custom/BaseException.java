package com.example.demo.exception.custom;

import com.example.demo.exception.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BaseException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
