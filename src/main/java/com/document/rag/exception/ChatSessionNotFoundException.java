package com.document.rag.exception;

import com.document.rag.exception.custom.BaseException;

public class ChatSessionNotFoundException
        extends BaseException {

    public ChatSessionNotFoundException() {
        super(
                ErrorCode.CHAT_SESSION_NOT_FOUND,
                "Chat session was not found."
        );
    }
}
