package com.to.reggie.config;

import com.to.reggie.common.JacksonObjectMapper;
import com.to.reggie.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    //消息转换器
    //前后端消息格式转换与协调
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("启动消息转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器 将java对象转json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //追加到mvc框架消息转换器容器中
        converters.add(0,messageConverter);

    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开启拦截器");
        //创建自定义拦截器对象
        HandlerInterceptor interceptor = new LoginInterceptor();

        //配置黑名单
        List<String> patterns = new ArrayList<>();
        patterns.add("/backend/page/category/**");
        patterns.add("/backend/page/combo/**");
        patterns.add("/backend/page/food/**");
        patterns.add("/backend/page/member/**");
        patterns.add("/backend/page/order/**");
        patterns.add("/backend/index.html");


        patterns.add("/front/index.html");
        patterns.add("/front/page/add-order.html");
        patterns.add("/front/page/address.html");
        patterns.add("/front/page/address-edit.html");
        patterns.add("/front/page/no-wify.html");
        patterns.add("/front/page/order.html");
        patterns.add("/front/page/pay-success.html");
        patterns.add("/front/page/user.html");

        //拦截器注册
        registry.addInterceptor(interceptor)
                .addPathPatterns(patterns);
    }

    /**
     * 配置拦截器
     */


    //设置静态资源映射
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        //地址映射 classpath即resources路径
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front");
    }
}
