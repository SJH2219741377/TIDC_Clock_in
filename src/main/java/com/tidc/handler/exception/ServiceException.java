package com.tidc.handler.exception;

/**
 * 一般错误
 */
public class ServiceException extends RuntimeException {

    private int code = 999;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(int code, String message) {
        this(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
