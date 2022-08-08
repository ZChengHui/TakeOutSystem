package com.to.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义对象处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("{insert}" + metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        Long operationId = BaseContext.getCurrentId();
        metaObject.setValue("createUser", operationId);
        metaObject.setValue("updateUser", operationId);

    }

    /**
     * 更新时填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        //log.info("{update}" + metaObject.toString());
        //Long id = Thread.currentThread().getId();
        //log.info("线程ID={}",id);
        Long operationId = BaseContext.getCurrentId();
        //log.info("当前登录用户ID={}",operationId);

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", operationId);

    }
}
