package com.to.reggie.service.ex;

//套餐重复
public class SetmealDuplicationException extends ServiceException{
    public SetmealDuplicationException() {
        super();
    }

    public SetmealDuplicationException(String message) {
        super(message);
    }

    public SetmealDuplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetmealDuplicationException(Throwable cause) {
        super(cause);
    }

    protected SetmealDuplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
