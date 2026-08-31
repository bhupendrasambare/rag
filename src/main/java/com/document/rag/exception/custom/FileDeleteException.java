package com.document.rag.exception.custom;

import com.document.rag.exception.ErrorCode;

public class FileDeleteException extends BaseException {

    public FileDeleteException() {
        super(
                ErrorCode.FILE_DELETE_FAILED,
                "Unable to delete the file.");
    }
}
