package shakemate.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import shakemate.model.ChatRoom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;

import com.google.gson.Gson;

@MultipartConfig
@WebServlet("/SendMessageServlet")
public class SendMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String URL = "jdbc:mysql://localhost:3306/shakemate_db?serverTimezone=Asia/Taipei";
	public static final String USER = "root";
	public static final String PASSWORD = "123456";   
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String roomIdStr = request.getParameter("roomId");
		String senderIdStr = request.getParameter("senderId");
		String content = request.getParameter("content");
		String base64Image = request.getParameter("img");
		InputStream imgStream = null;

		if (base64Image != null && base64Image.startsWith("data:image")) {
		    // 切掉開頭 data:image/png;base64,
		    String pureBase64 = base64Image.split(",")[1];
		    byte[] imageBytes = Base64.getDecoder().decode(pureBase64);
		    imgStream = new ByteArrayInputStream(imageBytes);
		}
		
		if (roomIdStr == null || senderIdStr == null || content == null || content.trim().isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少必要參數");
	        return; // ❌ 停止繼續往下執行
	    }
		
		int roomId = Integer.parseInt(roomIdStr);
		int senderId = Integer.parseInt(senderIdStr);
		
		
		Connection con = null;
		PreparedStatement pstmt = null;

		try {
			// 1. 載入驅動
			Class.forName(DRIVER);
//			System.out.println("載入成功");

			// 2. 建立連線
			con = DriverManager.getConnection(URL, USER, PASSWORD);
//			System.out.println("連線成功");

			// 3. 送出SQL指令
			if (base64Image.startsWith("data:image")) {
				pstmt = con.prepareStatement(
						"INSERT INTO chat_message (room_id, sender_id, content, img, sent_time) VALUES (?, ?, ?, ?, NOW())");
				pstmt.setInt(1, roomId); // 4. 塞參數
				pstmt.setInt(2, senderId);
				pstmt.setString(3, content);
				pstmt.setBlob(4, imgStream);
			} else {
				pstmt = con.prepareStatement(
						"INSERT INTO chat_message (room_id, sender_id, content, sent_time) VALUES (?, ?, ?, NOW())");
				pstmt.setInt(1, roomId); // 4. 塞參數
				pstmt.setInt(2, senderId);
				pstmt.setString(3, content);
			}
			
			int row = pstmt.executeUpdate(); // 5. 執行

			if (row == 1) {
				System.out.println("訊息已成功寫入資料庫!");
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("{\"status\":\"success\",\"message\":\"訊息收到囉！\"}");
			}
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 6. 關閉資源
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
		}
	}

}
