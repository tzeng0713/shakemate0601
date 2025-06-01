package com.shakemate.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/testshakemate?serverTimezone=Asia/Taipei";
    private static final String USER = "root";
    private static final String PASSWORD = "123456"; // ← 改成你實際密碼

    static {
        try {
            Class.forName(DRIVER); // 載入 JDBC Driver
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}