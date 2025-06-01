package com.shakemate.model;

import java.sql.SQLException;

import com.shakemate.VO.UserProfileVO;

public interface UserProfileDAO {
	public UserProfileVO findById(int userId) throws SQLException;
	public UserProfileVO getRandomUnmatchedUser(int currentUserId) throws SQLException;
}
