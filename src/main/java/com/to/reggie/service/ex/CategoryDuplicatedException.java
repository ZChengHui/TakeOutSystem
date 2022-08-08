package com.to.reggie.service.ex;

/**
 * 分类重复异常
 */
public class CategoryDuplicatedException extends ServiceException{

    public CategoryDuplicatedException() {
        super();
    }

    public CategoryDuplicatedException(String message) {
        super(message);
    }

    public CategoryDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public CategoryDuplicatedException(Throwable cause) {
        super(cause);
    }

    protected CategoryDuplicatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
