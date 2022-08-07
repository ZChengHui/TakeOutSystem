package com.to.reggie.controller;

import com.to.reggie.common.R;
import com.to.reggie.service.ex.*;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 控制层基类
 * 异常捕获处理
 */
public class BaseController {

    //异常拦截器
    @ExceptionHandler(value = {ServiceException.class})
    public R<Void> handleException(Exception e) {
        if (e instanceof UsernameDuplicatedException) {
            return R.ex(4000,e);
        } else if (e instanceof PasswordNotMatchException) {
            return R.ex(4001,e);
        } else if (e instanceof UserNotFoundException) {
            return R.ex(4002,e);
        }  else if (e instanceof InsertException) {
            return R.ex(4003,e);
        } else if (e instanceof UpdateException) {
            return R.ex(4004,e);
        } else if (e instanceof UserStatusException) {
            return R.ex(4005,e);
        }    else {
            return R.ex(9999,e);
        }
    }

}
