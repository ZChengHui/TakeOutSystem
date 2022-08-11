package com.to.reggie.service.ex;

import com.to.reggie.service.ex.ServiceException;

//地址簿不存在
public class AddressBookNotFoundException extends ServiceException {
    public AddressBookNotFoundException() {
        super();
    }

    public AddressBookNotFoundException(String message) {
        super(message);
    }

    public AddressBookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AddressBookNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AddressBookNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
