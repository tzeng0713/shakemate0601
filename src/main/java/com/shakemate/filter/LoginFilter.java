package com.shakemate.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public class LoginFilter implements Filter {
    private FilterConfig config;
    @Override
    public void init(FilterConfig config){
    }

    @Override
    public void destroy(){
        config = null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException{
        HttpServletRequest req = (HttpServletRequest) request; // ServletRequest 轉成HttpServletRequest
        HttpServletResponse res = (HttpServletResponse) response; // ServletResponse HttpServletResponse

        HttpSession session = req.getSession(); // 取得Session 物件
        Object account = session.getAttribute("account"); // 取得account物件
        if(account == null){ // 如果account沒有值
            session.setAttribute("location", req.getRequestURI()); // 取的前頁的資訊, req.getRequestURI() 是 Servlet 中用來取得 HTTP 請求的 URI 路徑
            res.sendRedirect(req.getContextPath()+ "/login/login.jsp"); // 要重導的網址
            return;
        }else{
            PrintWriter out = res.getWriter();
//            out.println("Hi "+ account);
//            out.println("<br><FORM METHOD='post' action='" +
//                    req.getContextPath()+  "/login/loginHandler'" +
//                    " style='margin-bottom: 0px;'><input type=submit name='login' value='logout'></FORM>");
            chain.doFilter(request, response);
        }

    }

}
