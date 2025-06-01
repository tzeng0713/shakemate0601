package com.shakemate.service;

import java.sql.SQLException;

import com.shakemate.model.MatchDAOImpl;

public class MatchService {

    private MatchDAOImpl matchDAO = new MatchDAOImpl();

    public boolean hasUserActed(int userId, int targetId) throws SQLException {
        return matchDAO.hasUserActed(userId, targetId);
    }

    public void insertLike(int userId, int targetId) throws SQLException {
        matchDAO.insertLike(userId, targetId);
    }

    public void insertDislike(int userId, int targetId) throws SQLException {
        matchDAO.insertDislike(userId, targetId);
    }

    public boolean hasLikedBack(int userId, int targetId) throws SQLException {
        return matchDAO.hasLikedBack(userId, targetId);
    }

    public int insertMatchRecord(int userId, int targetId) throws SQLException {
        return matchDAO.insertMatchRecord(userId, targetId);
    }

    public int createChatRoom(int userId, int targetId, int matchId) throws SQLException {
        return matchDAO.createChatRoom(userId, targetId, matchId);
    }
}
