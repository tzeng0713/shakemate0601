package com.shakemate.controller;

import com.google.gson.Gson;
import com.shakemate.VO.UserProfileVO;
import com.shakemate.service.MatchService;
import com.shakemate.model.UserProfileDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/MatchControllerServlet")
public class MatchControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        int currentUserId = Integer.parseInt(req.getParameter("currentUserId"));

        if ("getNext".equals(action)) {
            try {
                UserProfileVO profile = new UserProfileDAOImpl().getRandomUnmatchedUser(currentUserId);
                if (profile == null) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND, "沒有更多會員");
                    return;
                }
                String json = new Gson().toJson(profile);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(json);
            } catch (SQLException e) {
                e.printStackTrace();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "資料庫錯誤");
            }
        } else {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知的 action");
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        int currentUserId = Integer.parseInt(req.getParameter("currentUserId"));
        int targetId = Integer.parseInt(req.getParameter("targetId"));

        MatchService service = new MatchService();
        Map<String, Object> result = new HashMap<>();

        try {
            switch (action) {
                case "like" -> {
                    if (service.hasUserActed(currentUserId, targetId)) {
                        result.put("alreadyActed", true);
                    } else {
                        service.insertLike(currentUserId, targetId);
                        if (service.hasLikedBack(currentUserId, targetId)) {
                            int matchId = service.insertMatchRecord(currentUserId, targetId);
                            int roomId = service.createChatRoom(currentUserId, targetId, matchId);
                            result.put("matched", true);
                            result.put("roomId", roomId);
                        } else {
                            result.put("matched", false);
                        }
                    }
                }
                case "dislike" -> {
                    if (!service.hasUserActed(currentUserId, targetId)) {
                        service.insertDislike(currentUserId, targetId);
//                        UserProfileVO profile = new UserProfileDAOImpl().getRandomUnmatchedUser(currentUserId);
//                        if (profile == null) {
//                            res.sendError(HttpServletResponse.SC_NOT_FOUND, "沒有更多會員");
//                            return;
//                        }
//                        String json = new Gson().toJson(profile);
//                        res.setContentType("application/json");
//                        res.setCharacterEncoding("UTF-8");
//                        res.getWriter().write(json);
                    }
                    result.put("disliked", true);
                    
                }
                default -> {
                    res.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知的 action");
                    return;
                }
            }

            String json = new Gson().toJson(result);
            res.setContentType("application/json");
            res.setCharacterEncoding("UTF-8");
            res.getWriter().write(json);
            

        } catch (SQLException e) {
            e.printStackTrace();
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "配對過程錯誤");
        }
    }
}
