package com.to.reggie.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
public class R<T> {

    private Integer code; //状态码

    private String msg; //错误信息

    private T data; //数据

    private Map map = new HashMap(); //动态数据

    private static final Integer OK = 200;

    public static <T> R<T> ex(Integer code, Exception e) {
        R<T> r = new R<>();
        r.code = code;
        r.msg = e.getMessage();
        return r;
    }

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = OK;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
