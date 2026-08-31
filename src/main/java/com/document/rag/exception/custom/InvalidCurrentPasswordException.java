package com.document.rag.exception.custom;

import com.document.rag.exception.ErrorCode;

public class InvalidCurrentPasswordException extends BaseException {

    public InvalidCurrentPasswordException() {
        super(ErrorCode.INVALID_CURRENT_PASSWORD
                , "Invalid current password");
    }
}