package com.to.reggie.service.ex;
//购物车为空
public class ShoppingCartEmptyException extends ServiceException {
    public ShoppingCartEmptyException() {
        super();
    }

    public ShoppingCartEmptyException(String message) {
        super(message);
    }

    public ShoppingCartEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShoppingCartEmptyException(Throwable cause) {
        super(cause);
    }

    protected ShoppingCartEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
