package com.to.reggie.common;

/**
 * 利用线程维护同一执行流上的局部变量
 * 基于ThreadLocal封装工具类，获取当前登录用户的id
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //存值
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //取值
    public static Long getCurrentId() {
        return threadLocal.get();
    }

}
