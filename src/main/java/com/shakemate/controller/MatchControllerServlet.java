package com.shakemate.controller;

import com.google.gson.Gson;
import com.shakemate.VO.UserProfileVO;
import com.shakemate.service.MatchService;
import com.shakemate.model.UserProfileDAOImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/MatchControllerServlet")
public class MatchControllerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String action = req.getParameter("action");
		HttpSession session = req.getSession(); 
//        int currentUserId = Integer.parseInt(req.getParameter("currentUserId"));
		Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
        
//        int test = Integer.parseInt(req.getParameter("test"));
//        if (test == 2) {
//        	ArrayList<String> interests = new ArrayList<String>();
//        	ArrayList<String> personality = new ArrayList<String>();
//        	interests.add("打籃球");
//        	interests.add("攝影");
//        	personality.add("陽光");
//        	List<UserProfileVO> result = new UserProfileDAOImpl().prefer_matched(interests, personality);
//        	return;
//        }

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
    	
		HttpSession session = req.getSession(); 
//      int currentUserId = Integer.parseInt(req.getParameter("currentUserId"));
		Integer currentUserId = Integer.valueOf(session.getAttribute("account").toString());
    	
        // 如果是 JSON → 處理條件篩選
        if ("application/json".equals(req.getContentType())) {
        	System.out.println("hi");
            BufferedReader reader = req.getReader();
            StringBuilder jsonBuffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }

            Gson gson = new Gson();
            Map<String, Object> data = gson.fromJson(jsonBuffer.toString(), Map.class);

            String action = (String) data.get("action");
            if ("getFiltered".equals(action)) {
//                int currentUserId = ((Double) data.get("currentUserId")).intValue();
                Integer gender = null;
                try {
                    String genderStr = (String) data.get("gender");
                    if (genderStr != null && !genderStr.isBlank()) {
                        gender = Integer.parseInt(genderStr);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // 或略過
                }
                List<String> interests = (List<String>) data.get("interests");
                List<String> personality = (List<String>) data.get("personality");

                List<UserProfileVO> result = new UserProfileDAOImpl()
                        .prefer_matched(currentUserId, interests, personality, gender);

                String json = gson.toJson(result);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(json);
                return;
            }
        }

        // 如果不是 JSON → 處理 like/dislike（維持你原本的寫法）
        String action = req.getParameter("action");
//        int currentUserId = Integer.parseInt(req.getParameter("currentUserId"));
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
