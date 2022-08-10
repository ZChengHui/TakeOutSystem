package com.to.reggie.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object emp = request.getSession().getAttribute("employeeId");
        Object usr = request.getSession().getAttribute("userPhone");
        if (emp != null ) {
            return true;
        } else if (usr != null) {
            return true;
        } else {
            response.sendRedirect("/backend/page/login/login.html");
            return false;
        }
    }
}
