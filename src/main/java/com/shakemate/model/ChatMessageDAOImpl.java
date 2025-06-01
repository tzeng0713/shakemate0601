package com.shakemate.model;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import com.shakemate.VO.ChatMessageVO;
import com.shakemate.util.Util;

public class ChatMessageDAOImpl implements ChatMessageDAO {


    @Override
    public List<ChatMessageVO> findByRoomId(int roomId) {
        List<ChatMessageVO> list = new ArrayList<>();

        String sql = "SELECT * FROM chat_message WHERE room_id = ? ORDER BY sent_time ASC";

        try (Connection con = Util.getConnection();
        	 PreparedStatement pstmt = con.prepareStatement(sql)) {
        	
            pstmt.setInt(1, roomId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int messageId = rs.getInt("message_id");
                    int senderId = rs.getInt("sender_id");
                    String content = rs.getString("message_content");
                    if ("null".equals(content)) {
                        content = null;
                    }
                    String sentTime = rs.getString("sent_time");
                    boolean isRead = rs.getBoolean("is_read");

                    // 處理圖片欄位（可能為 null）
                    byte[] imgBytes = rs.getBytes("message_img");
                    String imgBase64 = null;
                    if (imgBytes != null && imgBytes.length > 0) {
                        imgBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imgBytes);
                    }

                    ChatMessageVO msg = new ChatMessageVO(messageId, roomId, senderId, content, imgBase64, sentTime, isRead);
                    list.add(msg);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public void insert(ChatMessageVO message) {
        String sql = "INSERT INTO chat_message (room_id, sender_id, message_content, message_img, sent_time, is_read) VALUES (?, ?, ?, ?, NOW(), ?)";

        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, message.getRoomId());
            pstmt.setInt(2, message.getSenderId());
            pstmt.setString(3, message.getContent());
            

            if (message.getImgBytes() != null && message.getImgBytes().length > 0) {
                pstmt.setBytes(4, message.getImgBytes()); // ✅ 原始圖片 byte[] 寫入 BLOB
            } else {
                pstmt.setNull(4, Types.BLOB);
            }
            
            pstmt.setInt(5, 0);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }
    
    @Override
    public void markMessagesAsRead(int userId, int roomId) throws SQLException {
        String sql = "UPDATE CHAT_MESSAGE SET IS_READ = 1 WHERE ROOM_ID = ? AND SENDER_ID != ? AND IS_READ = 0";
        try (Connection con = Util.getConnection();
           	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }
    }

    
}
