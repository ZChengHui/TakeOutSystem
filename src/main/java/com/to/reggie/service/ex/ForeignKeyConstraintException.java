package com.to.reggie.service.ex;
//外键关联异常
public class ForeignKeyConstraintException extends ServiceException{
    public ForeignKeyConstraintException() {
        super();
    }

    public ForeignKeyConstraintException(String message) {
        super(message);
    }

    public ForeignKeyConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForeignKeyConstraintException(Throwable cause) {
        super(cause);
    }

    protected ForeignKeyConstraintException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
