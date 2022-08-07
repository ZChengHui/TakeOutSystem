package com.to.reggie.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object obj = request.getSession().getAttribute("employeeId");
        if (obj == null) {
            response.sendRedirect("/backend/page/login/login.html");
            return false;
        } else {
            return true;
        }
    }
}
