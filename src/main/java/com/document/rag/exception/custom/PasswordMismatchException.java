package com.document.rag.exception.custom;

import com.document.rag.exception.ErrorCode;

public class PasswordMismatchException extends BaseException {

    public PasswordMismatchException() {
        super(ErrorCode.INVALID_NEW_AND_CONFIRM_PASSWORD
                , "New password and confirm password must match.");
    }
}