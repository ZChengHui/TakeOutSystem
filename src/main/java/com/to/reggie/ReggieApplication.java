package com.to.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j //日志调试 使用log输出控制台
@SpringBootApplication
@EnableTransactionManagement//数据库事务
@EnableCaching //开启缓存
//@ServletComponentScan // 扫描组件 如自定义过滤器
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        //log.info("hello");
    }
}
