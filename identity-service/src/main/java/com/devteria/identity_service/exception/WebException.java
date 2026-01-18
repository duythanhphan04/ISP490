package com.devteria.identity_service.exception;

public class WebException extends RuntimeException {
    private ErrorCode errorCode;

    public WebException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
