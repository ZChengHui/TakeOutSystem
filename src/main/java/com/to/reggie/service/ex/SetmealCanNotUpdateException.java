package com.to.reggie.service.ex;

//套餐正在销售，不可修改
public class SetmealCanNotUpdateException extends ServiceException{
    public SetmealCanNotUpdateException() {
        super();
    }

    public SetmealCanNotUpdateException(String message) {
        super(message);
    }

    public SetmealCanNotUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetmealCanNotUpdateException(Throwable cause) {
        super(cause);
    }

    protected SetmealCanNotUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
