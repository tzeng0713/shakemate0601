package com.shakemate.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.xdevapi.Result;

@WebServlet("/MatchServlet")
public class MatchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	public static final String URL = "jdbc:mysql://localhost:3306/shakemate_db?serverTimezone=Asia/Taipei";
	public static final String USER = "root";
	public static final String PASSWORD = "123456";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// 取得前端傳來的資料
		String action = request.getParameter("action");
		String targetIdStr = request.getParameter("targetId");

		int user1Id = 1; // 暫時將登入者 ID 寫死
		int user2Id = Integer.parseInt(targetIdStr);

		boolean isLike = "like".equals(action);

		if (!isLike) {
			System.out.println("使用者選擇不喜歡，不紀錄配對。");
			return;
		}

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
			// 新增一筆資料到資料庫
			pstmt = con.prepareStatement(
					"INSERT INTO match_table (user1_id, user2_id, matched_at, status) VALUES (?, ?, NOW(), 'matched')");
			pstmt.setInt(1, user1Id); // 4. 塞參數
			pstmt.setInt(2, user2Id);
			int row = pstmt.executeUpdate(); // 5. 執行

			if (row == 1) {
				response.setContentType("text/plain");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("配對資料收到囉！");
			}

			pstmt = con.prepareStatement("SELECT * FROM match_table WHERE user1_id = ? AND user2_id = ?");
			pstmt.setInt(1, user2Id); // 4. 塞參數
			pstmt.setInt(2, user1Id);
			rs = pstmt.executeQuery(); // 5. 執行

			if (rs.next()) {
				System.out.println("成功配對!");
				pstmt = con.prepareStatement("INSERT INTO chat_room (user1_id, user2_id, created_at) VALUES (?, ?, NOW())");
				pstmt.setInt(1, user2Id);
				pstmt.setInt(2, user1Id);
				pstmt.executeUpdate();
			} else {
				System.out.println("對方尚未與你配對!");
			}


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
