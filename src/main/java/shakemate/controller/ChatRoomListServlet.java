package shakemate.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import shakemate.model.ChatRoom;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;


import java.util.Base64;


@WebServlet("/ChatRoomListServlet")
public class ChatRoomListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String URL = "jdbc:mysql://localhost:3306/shakemate_db?serverTimezone=Asia/Taipei";
	public static final String USER = "root";
	public static final String PASSWORD = "123456";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        System.out.println("🚀 ChatRoomListServlet 開始執行"); // 可以設斷點在這

        String userIdParam = request.getParameter("currentUserId");

        if (userIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "缺少 currentUserId 參數");
            return;
        }

        int userId = Integer.parseInt(userIdParam); // ✅ 轉換成整數
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// 1. 載入驅動
			Class.forName(DRIVER);
//			System.out.println("載入成功");

			// 2. 建立連線
			con = DriverManager.getConnection(URL, USER, PASSWORD);
//			System.out.println("連線成功");

			// 3. 送出SQL指令
			String sql = """
				    SELECT 
				        cr.room_id,
				        cr.user1_id,
				        cr.user2_id,
				        u.user_id AS peer_id,
				        u.name AS peer_name,
				        u.img_1 AS peer_avatar
				    FROM chat_room cr
				    JOIN user u
				      ON u.user_id = CASE
				                      WHEN cr.user1_id = ? THEN cr.user2_id
				                      ELSE cr.user1_id
				                    END
				    WHERE cr.user1_id = ? OR cr.user2_id = ?
				    """;

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, userId); // 用來判斷對方是誰
			pstmt.setInt(2, userId); // WHERE cr.user1_id = ?
			pstmt.setInt(3, userId); // OR cr.user2_id = ?

			rs = pstmt.executeQuery(); // 5. 執行

			List<ChatRoom> list = new ArrayList<>();
			
			while (rs.next()) {
				int roomId = rs.getInt("room_id");
				int user1 = rs.getInt("user1_id");
				int user2 = rs.getInt("user2_id");
				int peerId = rs.getInt("peer_id");
				String peerName = rs.getString("peer_name");
				byte[] peerAvatar = rs.getBytes("peer_avatar");
				byte[] avatarBytes = rs.getBytes("peer_avatar");
				String peerAvatarBase64 = Base64.getEncoder().encodeToString(avatarBytes);			

				ChatRoom chatRoom = new ChatRoom(roomId, user1, user2, peerId, peerName, peerAvatarBase64);
				list.add(chatRoom);
			}

			
			Gson gson = new Gson();
			String json = gson.toJson(list); // 把 Java List 轉成 JSON 字串
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json); // 將 JSON 字串送出（給前端 fetch() 接）
			
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
