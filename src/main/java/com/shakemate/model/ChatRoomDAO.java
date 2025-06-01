package com.shakemate.model;

import java.util.List;

import com.shakemate.VO.ChatRoomVO;

public interface ChatRoomDAO {
	public List<ChatRoomVO> findByUserId(int userId);
}
