package com.tidc.handler.exception;

/**
 * 用户不存在异常
 */
public class UserNotExistException extends RuntimeException {

    public UserNotExistException(String msg) {
        super(msg);
    }

    public UserNotExistException() {
    }

}
