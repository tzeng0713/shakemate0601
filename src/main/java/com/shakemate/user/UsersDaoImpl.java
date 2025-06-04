package com.shakemate.user;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.shakemate.user.*;
import com.shakemate.util.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsersDaoImpl implements UsersDao {
    private static final String  GET_ALL = "SELECT * FROM USERS;";
    private static final String  GET_BY_ID = "SELECT * FROM USERS WHERE USER_ID = ?;";
    private static final String  GET_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL = ?;";
    private static final String  ADD_USER = "INSERT INTO USERS (" +
                                            "USERNAME, EMAIL, PWD, GENDER, BIRTHDAY, LOCATION," +
                                            "INTRO, CREATED_TIME, INTERESTS, PERSONALITY, UPDATED_TIME," +
                                            "USER_STATUS, POST_STATUS, AT_AC_STATUS, SELL_STATUS) VALUES" +
                                            "(?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?, CURRENT_TIMESTAMP,?,?,?,?);";
    private static final String UPDATE_BY_ADM = "UPDATE USERS SET " +
            "USERNAME= ?,  PWD= ?,  LOCATION= ?, INTRO=? , INTERESTS= ?, PERSONALITY= ?, UPDATED_TIME=CURRENT_TIMESTAMP,"+
            "USER_STATUS= ?, POST_STATUS= ?, AT_AC_STATUS= ?, SELL_STATUS=?  WHERE USER_ID = ?;";
    private static final String UPDATE_BY_USER = "UPDATE USERS SET " +
            "USERNAME= ?,  PWD= ?,  LOCATION= ?, INTRO=? , INTERESTS= ?, PERSONALITY= ?, UPDATED_TIME=CURRENT_TIMESTAMP,"+
            " WHERE USER_ID = ?;";


    private static DataSource ds= new ConnectionPool().getConnPool();

    public Users findByEmail(String email){
        Users user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = ds.getConnection();
            pstmt = conn.prepareStatement(GET_BY_EMAIL);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if(rs.next()){
                user = new Users();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPwd(rs.getString("PWD"));
                user.setGender(rs.getInt("GENDER"));
                user.setBirthday(rs.getDate("birthday"));
                user.setLocation(rs.getString("LOCATION"));
                user.setIntro(rs.getString("INTRO"));
                user.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
                user.setImg1(rs.getString("IMG1"));
                user.setImg2(rs.getString("IMG2"));
                user.setImg3(rs.getString("IMG3"));
                user.setImg4(rs.getString("IMG4"));
                user.setImg5(rs.getString("IMG5"));
                user.setInterests(rs.getString("INTERESTS"));
                user.setUpdatedTime(rs.getTimestamp("UPDATED_TIME"));
                user.setUserStatus(rs.getInt("USER_STATUS"));
                user.setPostStatus(rs.getBoolean("POST_STATUS"));
                user.setAtAcStatus(rs.getBoolean("AT_AC_STATUS"));
                user.setSellStatus(rs.getBoolean("SELL_STATUS"));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            closeResources(conn, pstmt, rs);
        }
        return user;
    }

    public void updateByUser(Users user){
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = ds.getConnection();
            stmt = conn.prepareStatement(UPDATE_BY_USER);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPwd());
            stmt.setString(3, user.getLocation());
            stmt.setString(4, user.getIntro());
            stmt.setString(5, user.getInterests());
            stmt.setString(6, user.getPersonality());
            stmt.setInt(7, user.getUserId());
            stmt.executeUpdate();

            System.out.println("Updated user id: " + user.getUserId() + "Successfully");

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            closeResources(conn, stmt);
        }

    }

    @Override
    public void updateByAdm(Users user){
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = ds.getConnection();
            stmt = conn.prepareStatement(UPDATE_BY_ADM);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPwd());
            stmt.setString(3, user.getLocation());
            stmt.setString(4, user.getIntro());
            stmt.setString(5, user.getInterests());
            stmt.setString(6, user.getPersonality());
            stmt.setInt(7, user.getUserStatus());
            stmt.setBoolean(8, user.getPostStatus());
            stmt.setBoolean(9, user.getAtAcStatus());
            stmt.setBoolean(10, user.getSellStatus());
            stmt.setInt(11, user.getUserId());
            stmt.executeUpdate();

            System.out.println("Updated user id: " + user.getUserId() + "Successfully");

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            closeResources(conn, stmt);
        }

    }

    public void addUser(Users user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = ds.getConnection();
            stmt = conn.prepareStatement(ADD_USER);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPwd());
            stmt.setInt(4, user.getGender());
            stmt.setDate(5, user.getBirthday());
            stmt.setString(6, user.getLocation());
            stmt.setString(7, user.getIntro());
            stmt.setString(8, user.getInterests());
            stmt.setString(9, user.getPersonality());
            stmt.setInt(10, user.getUserStatus());
            stmt.setBoolean(11, user.getPostStatus());
            stmt.setBoolean(12, user.getAtAcStatus());
            stmt.setBoolean(13, user.getSellStatus());
            stmt.executeUpdate();
            System.out.println("User " + user.getUsername() + " created");
        }catch(SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(conn, stmt);
        }
    }

    public Users getUserById(Integer id){
        Users user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try{
            conn = ds.getConnection();
            pstmt = conn.prepareStatement(GET_BY_ID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if(rs.next()){
                user = new Users();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPwd(rs.getString("PWD"));
                user.setGender(rs.getInt("GENDER"));
                user.setBirthday(rs.getDate("birthday"));
                user.setLocation(rs.getString("LOCATION"));
                user.setIntro(rs.getString("INTRO"));
                user.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
                user.setImg1(rs.getString("IMG1"));
                user.setImg2(rs.getString("IMG2"));
                user.setImg3(rs.getString("IMG3"));
                user.setImg4(rs.getString("IMG4"));
                user.setImg5(rs.getString("IMG5"));
                user.setInterests(rs.getString("INTERESTS"));
                user.setUpdatedTime(rs.getTimestamp("UPDATED_TIME"));
                user.setUserStatus(rs.getInt("USER_STATUS"));
                user.setPostStatus(rs.getBoolean("POST_STATUS"));
                user.setAtAcStatus(rs.getBoolean("AT_AC_STATUS"));
                user.setSellStatus(rs.getBoolean("SELL_STATUS"));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            closeResources(conn, pstmt, rs);
        }
        return user;
    }

    public List<Users> getAllUsers(){
        List<Users> users = new ArrayList<Users>();
        Users user = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ds.getConnection();
            pstmt = conn.prepareStatement(GET_ALL);
            rs = pstmt.executeQuery();

            while(rs.next()){
                user = new Users();
                user.setUserId(rs.getInt("USER_ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPwd(rs.getString("PWD"));
                user.setGender(rs.getInt("GENDER"));
                user.setBirthday(rs.getDate("birthday"));
                user.setLocation(rs.getString("LOCATION"));
                user.setIntro(rs.getString("INTRO"));
                user.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
                user.setImg1(rs.getString("IMG1"));
                user.setImg2(rs.getString("IMG2"));
                user.setImg3(rs.getString("IMG3"));
                user.setImg4(rs.getString("IMG4"));
                user.setImg5(rs.getString("IMG5"));
                user.setInterests(rs.getString("INTERESTS"));
                user.setUpdatedTime(rs.getTimestamp("UPDATED_TIME"));
                user.setUserStatus(rs.getInt("USER_STATUS"));
                user.setPostStatus(rs.getBoolean("POST_STATUS"));
                user.setAtAcStatus(rs.getBoolean("AT_AC_STATUS"));
                user.setSellStatus(rs.getBoolean("SELL_STATUS"));
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeResources(conn, pstmt, rs);
        }
        return users;
    }




    private void closeResources(Connection con, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException se) {
                se.printStackTrace(System.err);
            }
        }
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace(System.err);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private void closeResources(Connection con, PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException se) {
                se.printStackTrace(System.err);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
