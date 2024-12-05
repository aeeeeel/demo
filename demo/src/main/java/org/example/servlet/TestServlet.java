package org.example.servlet;

import lombok.extern.java.Log;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

@Log
@WebServlet(value = "/test")
public class TestServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //首先将其转换为HttpServletRequest（继承自ServletRequest，一般是此接口实现）
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        System.out.println(request.getProtocol());  //获取协议版本
        System.out.println(request.getRemoteAddr());  //获取访问者的IP地址
        System.out.println(request.getMethod());   //获取请求方法
        //获取头部信息
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            System.out.println(name + ": " + request.getHeader(name));
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write("response message");

    }
    @Override
    public String getServletInfo() {
        return "";
    }

    @Override
    public void destroy() {

    }
}
