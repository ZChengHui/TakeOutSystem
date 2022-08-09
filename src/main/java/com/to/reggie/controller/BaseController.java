package com.to.reggie.controller;

import com.to.reggie.common.R;
import com.to.reggie.controller.ex.*;
import com.to.reggie.service.ex.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

/**
 * 控制层基类
 * 异常捕获处理
 */
@Slf4j
public class BaseController {

    //异常拦截器
    @ExceptionHandler(value = {ServiceException.class})
    public R<Void> handleException(Exception e) {
        HashMap<Object, R> hashMap = new HashMap<>();
        hashMap.put(UsernameDuplicatedException.class, R.ex(4000, e));
        hashMap.put(PasswordNotMatchException.class, R.ex(4001, e));
        hashMap.put(UserNotFoundException.class, R.ex(4002, e));
        hashMap.put(InsertException.class, R.ex(4003, e));
        hashMap.put(UpdateException.class, R.ex(4004, e));
        hashMap.put(UserStatusException.class, R.ex(4005, e));
        hashMap.put(ForeignKeyConstraintException.class, R.ex(4006, e));
        hashMap.put(DeleteException.class, R.ex(4007, e));
        hashMap.put(CategoryDuplicatedException.class, R.ex(4008, e));

        hashMap.put(FileEmptyException.class, R.ex(5001, e));
        hashMap.put(FileStateException.class, R.ex(5002, e));
        hashMap.put(FileTransferException.class, R.ex(5003, e));
        hashMap.put(FileSizeException.class, R.ex(5004, e));
        hashMap.put(FileTypeException.class, R.ex(5005, e));

        if (hashMap.get(e.getClass()) != null) {
            return hashMap.get(e.getClass());
        } else {
            return R.ex(9999, e);
        }
//        if (e instanceof UsernameDuplicatedException) {
//            return R.ex(4000,e);
//        } else if (e instanceof PasswordNotMatchException) {
//            return R.ex(4001,e);
//        } else if (e instanceof UserNotFoundException) {
//            return R.ex(4002,e);
//        }  else if (e instanceof InsertException) {
//            return R.ex(4003,e);
//        } else if (e instanceof UpdateException) {
//            return R.ex(4004,e);
//        } else if (e instanceof UserStatusException) {
//            return R.ex(4005,e);
//        }    else {
//            return R.ex(9999,e);
//        }

    }

}
