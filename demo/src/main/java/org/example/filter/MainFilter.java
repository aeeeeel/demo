package org.example.filter;

import org.example.entity.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class MainFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String url = req.getRequestURL().toString();
        //判断是否为静态资源
        if(!url.endsWith(".js") && !url.endsWith(".css") && !url.endsWith(".png")){
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            //判断是否未登陆
            if(user == null && !url.endsWith("Login")){
                res.sendRedirect("Login");
                return;
            }
        }
        //交给过滤链处理
        chain.doFilter(req, res);
    }
}

