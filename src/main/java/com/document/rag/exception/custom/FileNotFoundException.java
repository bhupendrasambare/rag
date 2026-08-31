package com.document.rag.exception.custom;

import com.document.rag.exception.ErrorCode;

public class FileNotFoundException extends BaseException {

    public FileNotFoundException() {
        super(
                ErrorCode.FILE_NOT_FOUND,
                "Requested file was not found.");
    }
}
