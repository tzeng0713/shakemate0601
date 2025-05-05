package shakemate.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shakemate.model.ChatMessage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


@WebServlet("/GetRoomMessageServlet")
public class GetRoomMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String URL = "jdbc:mysql://localhost:3306/shakemate_db?serverTimezone=Asia/Taipei";
	public static final String USER = "root";
	public static final String PASSWORD = "123456";
   
   

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		System.out.println("🚀 GetRoomMessageServlet 開始執行"); // 可以設斷點在這

		String roomIdStr = request.getParameter("roomId");
		System.out.println("收到 roomId：" + roomIdStr);
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			int roomId = Integer.parseInt(roomIdStr);
			
			// 1. 載入驅動
			Class.forName(DRIVER);
//			System.out.println("載入成功");

			// 2. 建立連線
			con = DriverManager.getConnection(URL, USER, PASSWORD);
//			System.out.println("連線成功");

			// 3. 送出SQL指令
			pstmt = con.prepareStatement("SELECT * FROM chat_message WHERE room_id = ? ORDER BY sent_time");
			pstmt.setInt(1, roomId); // 4. 塞參數
			rs = pstmt.executeQuery(); // 5. 執行

			List<ChatMessage> list = new ArrayList<>();

			while (rs.next()) {
				int messageId = rs.getInt("message_id");
				roomId = rs.getInt("room_id");
				int senderId = rs.getInt("sender_id");
				String content = rs.getString("content");
				String sentTime = rs.getString("sent_time");
				ChatMessage chatMessage = new ChatMessage(messageId, roomId, senderId, content, sentTime);
				list.add(chatMessage);
			}

			Gson gson = new Gson();
			String json = gson.toJson(list); // 把 Java List 轉成 JSON 字串
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json); // 將 JSON 字串送出（給前端 fetch() 接）

		} catch (NumberFormatException e) {
			System.out.println("roomId 轉換失敗！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 6. 關閉資源
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}
			}
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
