package com.to.reggie.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 废弃过滤器
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        //1.获取本次请求URI
        String requestURI = request.getRequestURI();

        log.info("有请求：{}", requestURI);

        //2.判断本次请求是否需要处理
        //白名单
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        //路径匹配函数
        Boolean check = check(requestURI, urls);

        //3.如果不需要处理，则直接放行
        if (check) {
            log.info("白名单请求：{}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        //4.判断登录状态，如果已登录，则直接放行
        Object session = request.getSession().getAttribute("employeeId");
        if (session != null) {
            log.info("放行登录请求：{}", requestURI);
            log.info("用户ID：{}",request.getSession().getAttribute("employeeId").toString());
            filterChain.doFilter(request, response);
            return;
        }
        //5.如果未登录，则返回未登录结果，通过输出流方式向客户端页面响应数据
        else {
            log.info("拦截未登录请求：{}", requestURI);
            //response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }

        //log.info("拦截到新请求：{}",request.getRequestURI());
        //log.info("拦截到新请求：{}",request.getRequestURL());完整请求路径
        //filterChain.doFilter(request, response);//放行
    }

    /**
     * 路径匹配函数
     * @param requestURI
     * @param urls
     * @return
     */
    public Boolean check(String requestURI, String[] urls) {
        for (String url : urls) {
            Boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
