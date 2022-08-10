package com.to.reggie.service.ex;

//菜品名称重复
public class DishNameDuplicatedException extends ServiceException{
    public DishNameDuplicatedException() {
        super();
    }

    public DishNameDuplicatedException(String message) {
        super(message);
    }

    public DishNameDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DishNameDuplicatedException(Throwable cause) {
        super(cause);
    }

    protected DishNameDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
