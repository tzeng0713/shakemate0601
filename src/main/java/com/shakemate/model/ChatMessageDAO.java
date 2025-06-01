package com.shakemate.model;

import java.sql.SQLException;
import java.util.List;

import com.shakemate.VO.ChatMessageVO;

public interface ChatMessageDAO {
	public List<ChatMessageVO> findByRoomId(int roomId);
	public void insert(ChatMessageVO message);
	public void markMessagesAsRead(int userId, int roomId) throws SQLException;
}
