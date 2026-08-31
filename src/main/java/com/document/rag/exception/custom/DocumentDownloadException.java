package com.document.rag.exception.custom;

import com.document.rag.exception.ErrorCode;

public class DocumentDownloadException extends BaseException {

    public DocumentDownloadException() {
        super(
                ErrorCode.DOCUMENT_DOWNLOAD_FAILED,
                "Unable to download the document.");
    }
}
