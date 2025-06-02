package com.shakemate.model;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shakemate.VO.UserProfileVO;
import com.shakemate.util.Util;

import jakarta.servlet.http.HttpServletResponse;

public class MatchDAOImpl implements MatchDAO{


	// 檢查是否已對某位使用者按過 like 或 dislike
	@Override
	public boolean hasUserActed(int userId, int targetId) throws SQLException {
		String sql = "SELECT 1 FROM user_matches WHERE ACTION_USER_ID = ? AND TARGET_USER_ID = ?";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, targetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
	}
	
	// 對某位使用者送出 like 行為
	@Override
    public void insertLike(int userId, int targetId) throws SQLException {
        String sql = "INSERT IGNORE INTO user_matches (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES (?, ?, 0, NOW())";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, targetId);
            pstmt.executeUpdate();
        }
    }
    
	// 對某位使用者送出 dislike 行為
	@Override
    public void insertDislike(int userId, int targetId) throws SQLException {
        String sql = "INSERT IGNORE INTO user_matches (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES (?, ?, 1, NOW())";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, targetId);
            pstmt.executeUpdate();
        }
    }
    
    // 檢查對方是否也 like 過我
	@Override
    public boolean hasLikedBack(int userId, int targetId) throws SQLException {
        String sql = "SELECT 1 FROM user_matches WHERE ACTION_USER_ID = ? AND TARGET_USER_ID = ? AND ACTION_TYPE = 0";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, targetId); // 對方是否對我按過 like
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    // 雙方成功配對，插入配對成功紀錄（action_type = 2），回傳 match_id
	@Override
    public int insertMatchRecord(int userId, int targetId) throws SQLException {
    	// Step 1. 先檢查這筆配對成功的紀錄是否已存在（避免重複 insert）
        String checkSql = "SELECT MATCH_ID FROM user_matches WHERE ACTION_USER_ID = ? AND TARGET_USER_ID = ? AND ACTION_TYPE = 2";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(checkSql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, targetId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("MATCH_ID"); // 已存在 → 回傳原 matchId
                }
            }
        }

        // Step 2. 若不存在，才真正插入新的配對成功紀錄
        String insertSql = "INSERT INTO user_matches (ACTION_USER_ID, TARGET_USER_ID, ACTION_TYPE, ACTION_TIME) VALUES (?, ?, 2, NOW())";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, targetId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // 成功插入 → 回傳新的 matchId
                }
            }
        }
        return -1; // 表示插入失敗
    }
    
    //根據 matchId 建立一間聊天室
	@Override
    public int createChatRoom(int userId, int targetId, int matchId) throws SQLException {
        String sql = "INSERT INTO chat_room (USER1_ID, USER2_ID, MATCH_ID, CREATED_TIME, ROOM_STATUS) VALUES (?, ?, ?, NOW(), 0)";
        try (Connection con = Util.getConnection();
              	 PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int minId = Math.min(userId, targetId);
            int maxId = Math.max(userId, targetId);

            pstmt.setInt(1, minId);
            pstmt.setInt(2, maxId);
            pstmt.setInt(3, matchId);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1); // 回傳 room_id
                }
            }
        }
        return -1; // 表示建立聊天室失敗
    }
}
