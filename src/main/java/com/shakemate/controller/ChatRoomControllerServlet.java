package com.shakemate.controller;


import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.shakemate.VO.ChatMessageVO;
import com.shakemate.VO.ChatRoomVO;
import com.shakemate.VO.UserProfileVO;
import com.shakemate.model.ChatMessageDAO;
import com.shakemate.model.ChatMessageDAOImpl;
import com.shakemate.model.ChatRoomDAO;
import com.shakemate.model.ChatRoomDAOImpl;
import com.shakemate.model.UserProfileDAO;
import com.shakemate.model.UserProfileDAOImpl;
import com.shakemate.util.Util;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/ChatRoomControllerServlet")
@MultipartConfig
public class ChatRoomControllerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		String action = req.getParameter("action");

		
		try {
		    switch (action) {
		        case "list" -> handleChatRoomList(req, res);
		        case "getMessages" -> handleGetMessages(req, res);
		        case "getUserProfile" -> handleGetUserProfile(req, res);
		        default -> res.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知的 action 參數");
		    }
		} catch (Exception e) {
			e.printStackTrace();
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "資料庫錯誤");
		}
		
	}
	

	


	protected void doPost(HttpServletRequest req, HttpServletResponse res)
	        throws ServletException, IOException {
	    String action = req.getParameter("action");

	    try {
	        if ("send".equals(action)) {
	            handleSendMessage(req, res);
	        } else if ("markAsRead".equals(action)) {
	        	handleMarkAsRead(req, res);
	        } else {
	            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "未知的 action 參數");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    }
	}

	
	
	private void handleChatRoomList(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		String userIdStr = req.getParameter("currentUserId");
		if (userIdStr == null) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少 currentUserId 參數");
			return;
		}
		int userId = Integer.parseInt(userIdStr);

		// 呼叫 DAO
		ChatRoomDAO dao = new ChatRoomDAOImpl();
		List<ChatRoomVO> list = dao.findByUserId(userId);

		// 回傳 JSON
		Gson gson = new Gson();
		String json = gson.toJson(list);
		res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write(json);
	}
	
	
	private void handleGetMessages(HttpServletRequest req, HttpServletResponse res) 
			throws IOException{
		String roomIdStr = req.getParameter("roomId");
	    if (roomIdStr == null) {
	        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少 roomId 參數");
	        return;
	    }

	    try {
	        int roomId = Integer.parseInt(roomIdStr);

	        ChatMessageDAO dao = new ChatMessageDAOImpl();
	        List<ChatMessageVO> messages = dao.findByRoomId(roomId);

	        Gson gson = new Gson();
	        String json = gson.toJson(messages);
	        res.setContentType("application/json");
	        res.setCharacterEncoding("UTF-8");
	        res.getWriter().write(json);

	    } catch (NumberFormatException e) {
	        System.out.println("roomId 轉換失敗！");
	        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "roomId 格式錯誤");
	    }
	}
	
	private void handleSendMessage(HttpServletRequest req, HttpServletResponse res)
	        throws IOException, ServletException {
	    String roomIdStr = req.getParameter("roomId");
	    String senderIdStr = req.getParameter("senderId");
	    String content = req.getParameter("content");

	    if (roomIdStr == null || senderIdStr == null) {
	        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少參數");
	        return;
	    }

	    try {
	        int roomId = Integer.parseInt(roomIdStr);
	        int senderId = Integer.parseInt(senderIdStr);
	        
	        byte[] imgBytes = null;
	        Part imgPart = req.getPart("img");
	        if (imgPart != null && imgPart.getSize() > 0) {
	            try (InputStream in = imgPart.getInputStream()) {
	                imgBytes = in.readAllBytes(); // ✅ 保留原始圖片位元組（bytes）
	            }
	        }
	        ChatMessageVO msg = new ChatMessageVO();
	        msg.setRoomId(roomId);
	        msg.setSenderId(senderId);
	        msg.setContent(content);
	        msg.setImgBytes(imgBytes);

	        ChatMessageDAO dao = new ChatMessageDAOImpl();
	        dao.insert(msg);

	        res.setStatus(HttpServletResponse.SC_OK);

	    } catch (NumberFormatException e) {
	        res.sendError(HttpServletResponse.SC_BAD_REQUEST, "格式錯誤");
	    }
	}
	
	private void handleGetUserProfile(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
	    String userIdStr = req.getParameter("currentUserId");

	    if (userIdStr == null) {
	        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	    }

	    try {
	        int userId = Integer.parseInt(userIdStr);

	        // 呼叫 DAO
	        UserProfileDAO dao = new UserProfileDAOImpl();
	        UserProfileVO vo = dao.findById(userId);

	        if (vo == null) {
	            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
	            return;
	        }

	        Gson gson = new Gson();
	        String json = gson.toJson(vo);
	        res.setContentType("application/json");
	        res.setCharacterEncoding("UTF-8");
	        res.getWriter().write(json);
	        // 回傳物件 → 給 doGet 中統一處理成 JSON
//	        return profile;

	    } catch (NumberFormatException | SQLException e) {
	        e.printStackTrace();
	        res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	    }
	}
	
	// 處理訊息被已讀的情況
	private void handleMarkAsRead(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		String userIdStr = req.getParameter("currentUserId");
		String roomIdStr = req.getParameter("roomId");
		
		if (userIdStr == null || roomIdStr == null) {
	        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        return;
	    }
		
		int userId = Integer.parseInt(userIdStr);
		int roomId = Integer.parseInt(roomIdStr);
		
		try {
			ChatMessageDAO dao = new ChatMessageDAOImpl();
			dao.markMessagesAsRead(userId, roomId);
			res.getWriter().write("success");
		} catch (SQLException e) {
			e.printStackTrace();
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.getWriter().write("error");
		}
	}
	
	
}
