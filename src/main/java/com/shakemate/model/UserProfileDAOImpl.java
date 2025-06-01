package com.shakemate.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shakemate.VO.UserProfileVO;
import com.shakemate.util.Util;

public class UserProfileDAOImpl implements UserProfileDAO {


    @Override
    public UserProfileVO findById(int userId) throws SQLException {
        String sql = "SELECT username, birthday, personality, interests, intro, img1, img2, img3, img4, img5 FROM users WHERE user_id = ?";
        
        try (Connection con = Util.getConnection();
              	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    Date birthday = rs.getDate("birthday");
                    String personality = rs.getString("personality");
                    String interests = rs.getString("interests");
                    String intro = rs.getString("intro");

                    int age = Period.between(birthday.toLocalDate(), LocalDate.now()).getYears();
                    String zodiac = getZodiac(birthday.toLocalDate());

                    List<String> avatarList = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        String imgUrl = rs.getString("img" + i);
                        if (imgUrl != null && !imgUrl.isBlank()) {
                            avatarList.add(imgUrl);
                        }
                    }

                    return new UserProfileVO(userId, username, age, zodiac, avatarList, personality, interests, intro);
                }
            }
        }
        return null;
    }

    private String getZodiac(LocalDate birthday) {
        int month = birthday.getMonthValue();
        int day = birthday.getDayOfMonth();

        return switch (month) {
            case 1 -> (day < 20) ? "摩羯座" : "水瓶座";
            case 2 -> (day < 19) ? "水瓶座" : "雙魚座";
            case 3 -> (day < 21) ? "雙魚座" : "牡羊座";
            case 4 -> (day < 20) ? "牡羊座" : "金牛座";
            case 5 -> (day < 21) ? "金牛座" : "雙子座";
            case 6 -> (day < 21) ? "雙子座" : "巨蟹座";
            case 7 -> (day < 23) ? "巨蟹座" : "獅子座";
            case 8 -> (day < 23) ? "獅子座" : "處女座";
            case 9 -> (day < 23) ? "處女座" : "天秤座";
            case 10 -> (day < 23) ? "天秤座" : "天蠍座";
            case 11 -> (day < 22) ? "天蠍座" : "射手座";
            case 12 -> (day < 22) ? "射手座" : "摩羯座";
            default -> "未知";
        };
    }
    
    @Override
    public UserProfileVO getRandomUnmatchedUser(int currentUserId) throws SQLException {
        String sql = """
            SELECT user_id, username, birthday, personality, interests, intro,
                   img1, img2, img3, img4, img5
            FROM users
            WHERE user_id != ?
              AND user_id NOT IN (
                  SELECT TARGET_USER_ID FROM user_matches WHERE ACTION_USER_ID = ?
              )
            ORDER BY RAND()
            LIMIT 1
        """;

        try (Connection con = Util.getConnection();
              	 PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, currentUserId);
            pstmt.setInt(2, currentUserId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String username = rs.getString("username");
                    Date birthday = rs.getDate("birthday");
                    int age = Period.between(birthday.toLocalDate(), LocalDate.now()).getYears();
                    String zodiac = getZodiac(birthday.toLocalDate());
                    String personality = rs.getString("personality");
                    String interests = rs.getString("interests");
                    String intro = rs.getString("intro");

                    List<String> avatarList = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        String url = rs.getString("img" + i);
                        if (url != null && !url.isBlank()) {
                            avatarList.add(url);
                        }
                    }

                    return new UserProfileVO(userId, username, age, zodiac, avatarList, personality, interests, intro);
                }
            }
        }

        return null;
    }


    
//    @Override
//    public Map<String, Object> findById(int userId) throws SQLException {
//        String sql = "SELECT name, birthday, job, personality, interests, intro, img_1 FROM user WHERE user_id = ?";
//        Map<String, Object> map = new HashMap<>();
//        
//        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
//            pstmt.setInt(1, userId);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//    				Date bd = rs.getDate("birthday");
//    				int age = Period.between(bd.toLocalDate(), LocalDate.now()).getYears();
//
//    				map.put("name", rs.getString("name"));
//    				map.put("age", age);
//    				map.put("job", rs.getString("job"));
//    				map.put("personality", rs.getString("personality"));
//    				map.put("interests", rs.getString("interests"));
//    				map.put("intro", rs.getString("intro"));
//    				
//    				
//    				// —— 安全讀 BLOB 並轉 Data URL ——  
//    			    byte[] imgBytes = rs.getBytes("img_1");
//    			    String imgBase64 = null;
//    			    if (imgBytes != null && imgBytes.length > 0) {
//    			        imgBase64 = "data:image/jpeg;base64," 
//    			            + Base64.getEncoder().encodeToString(imgBytes);
//    			        map.put("imgBase64", imgBase64);
//    			    }
//    			
//    			}
//                return map;
//            }
//        }
//    }
}
