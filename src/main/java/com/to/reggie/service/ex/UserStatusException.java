package com.to.reggie.service.ex;

/**
 * 用户状态异常
 */
public class UserStatusException extends ServiceException{

    public UserStatusException() {
        super();
    }

    public UserStatusException(String message) {
        super(message);
    }

    public UserStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserStatusException(Throwable cause) {
        super(cause);
    }

    protected UserStatusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
