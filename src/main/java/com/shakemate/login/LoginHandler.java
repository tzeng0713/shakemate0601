package com.shakemate.login;



import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

import com.shakemate.user.*;


@WebServlet(name = "loginHandler", value = "/login/loginHandler")
public class LoginHandler extends HttpServlet{
    private static final long serialVersionUID = 1L;

    private UserService userService;
    @Override
    public void init(){
        userService = new UserService(); // 再init時先把要用的Service先new
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doPost(req, res);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        switch (req.getParameter("login")) {
        case "login": login(req, res, out);
        break;
        case  "logout" : logout(req, res, out);
        break;
        default: res.sendRedirect(req.getContextPath() + "/index.jsp");
    }


    }
    
    private void logout(HttpServletRequest req, HttpServletResponse res, PrintWriter out) throws IOException{
        HttpSession session = req.getSession();
        Object accountObj = session.getAttribute("account");
        if(accountObj != null){
            session.removeAttribute("account");
            out.println("<HTML><HEAD><TITLE>Access Denied</TITLE></HEAD>");
            out.println("<BODY><H1> 登出成功 </H1>");
            out.println("重新登入:  <A HREF=" + req.getContextPath() + "/login/login.jsp>Login</A>");
            out.println("回首頁:  <A HREF=" + req.getContextPath() + "/index.jsp>Home</A>");
            out.println("</BODY></HTML>");
        }else{
            res.sendRedirect(req.getContextPath() + "/index.jsp");
        }
    }
    
    
    private void login(HttpServletRequest req, HttpServletResponse res, PrintWriter out) throws IOException{
        String account = req.getParameter("account"); // 取得request中 account(帳號)的值
        String password = req.getParameter("password"); // 取得request中 password(密碼)的值
        Users user = checkUserExist(account);
        if(user == null || !(usrPermission(user, password))){ // 登入失敗(先檢查使用者是否有存在 || 再檢查密碼是否正確)
            out.println("<HTML><HEAD><TITLE>Access Denied</TITLE></HEAD>");
            out.println("<BODY><H1> 登入失敗，請檢查帳號密碼是否正確 </H1>");
            out.println("重新登入:  <A HREF=" + req.getContextPath() + "/login/login.jsp>Login</A>");
            out.println("</BODY></HTML>");
        }else{ // 登入成功
            HttpSession session = req.getSession();
            session.setAttribute("account", user.getUserId()); // 這裡的登錄的attribute的先用 userName

            // 這裡是設定重導回前面拜訪過的頁面
            try{
                String location = (String)session.getAttribute("location");
                if(location != null){
                    res.sendRedirect(location);
                    return;
                }
            }catch(Exception ignored){}

            res.sendRedirect(req.getContextPath() + "/index.jsp"); // 如果沒有要返回的頁面
        }
    }

    private Users checkUserExist(String account){ // 單純為了方便閱讀才用成方法
        return userService.getUserByEmail(account); // Service中的用email取得Users物件
    }
    protected boolean usrPermission(Users user, String password){ // 單純為了方便閱讀才用成方法
        return userService.login(user.getEmail(), password); // Service中的login是用來比對密碼是否符合
    }
}
