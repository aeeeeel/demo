package org.example.servlet;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.example.entity.User;
import org.example.mapper.UserMapper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;

@WebServlet("/Login")
@Log
public class LoginServlet extends HttpServlet {
    SqlSessionFactory factory;
    @SneakyThrows
    @Override
    public void init() throws   ServletException {
        factory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader("mybatis-config.xml"));
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            String username = null;
            String password = null;
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("username")) username = cookie.getValue();
                if(cookie.getName().equals("password")) password = cookie.getValue();
            }
            if(username != null && password != null){
                //登陆校验
                try (SqlSession sqlSession = factory.openSession(true)){
                    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
                    User user = mapper.getUser(username, password);
                    if(user != null){
                        resp.sendRedirect("time");
                        return;   //直接返回
                    }
                    else{
                        Cookie cookie_username = new Cookie("username", username);
                        cookie_username.setMaxAge(0);
                        Cookie cookie_password = new Cookie("password", password);
                        cookie_password.setMaxAge(0);
                        resp.addCookie(cookie_username);
                        resp.addCookie(cookie_password);
                    }
                }
            }
        }
        req.getRequestDispatcher("/").forward(req, resp);   //正常情况还是转发给默认的Servlet帮我们返回静态页面
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //首先设置一下响应类型
        resp.setContentType("text/html;charset=UTF-8");
        //获取POST请求携带的表单数据
        Map<String, String[]> map = req.getParameterMap();
        //判断表单是否完整
        if(map.containsKey("username") && map.containsKey("password")) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            //登陆校验（待完善）
            try (SqlSession sqlSession = factory.openSession(true)){
                UserMapper mapper = sqlSession.getMapper(UserMapper.class);
                User user = mapper.getUser(username, password);
                //判断用户是否登陆成功，若查询到信息则表示存在此用户
                if(user != null){
                    if(map.containsKey("remember-me")){   //若勾选了勾选框，那么会此表单信息
                        Cookie cookie_username = new Cookie("username", username);
                        cookie_username.setMaxAge(30);
                        Cookie cookie_password = new Cookie("password", password);
                        cookie_password.setMaxAge(3000);
                        resp.addCookie(cookie_username);
                        resp.addCookie(cookie_password);
                    }

                    resp.getWriter().write("登陆成功！");
                    ServletContext context = getServletContext();
                    context.setAttribute("test", "我是重定向之前的数据");
                    resp.sendRedirect("time");
                    System.out.println(getServletContext().getAttribute("test"));

                    //ServletContext context = getServletContext();
                    //context.getRequestDispatcher("/time").forward(req, resp);


//                    req.setAttribute("test", "我是请求转发前的数据");
//                    req.getRequestDispatcher("/time").forward(req, resp);
//                    System.out.println(req.getAttribute("test"));

                    HttpSession session = req.getSession();
                    session.setAttribute("user", user);


                }else {
                    resp.getWriter().write("登陆失败，请验证您的用户名或密码！");
                }
            }

            //权限校验（待完善）
        }else {
            resp.getWriter().write("错误，您的表单数据不完整！");
        }


    }



}
