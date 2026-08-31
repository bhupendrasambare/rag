package com.document.rag.exception.custom;

import com.document.rag.exception.ErrorCode;

public class FileStorageException extends BaseException {

    public FileStorageException(String message) {
        super(ErrorCode.FILE_STORAGE_FAILED, message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(ErrorCode.FILE_STORAGE_FAILED, message);
    }
}
