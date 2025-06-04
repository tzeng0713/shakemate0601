package com.shakemate.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

@WebListener
public class MyContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[MyContextListener] Cleaning up JDBC driver...");

        // 1. Unregister JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Unregistered JDBC driver: " + driver);
            } catch (SQLException e) {
                System.err.println("Error deregistering driver: " + driver);
                e.printStackTrace();
            }
        }

        // 2. Shutdown MySQL cleanup thread
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
            System.out.println("MySQL AbandonedConnectionCleanupThread shut down.");
        } catch (Exception e) {
            System.err.println("Error shutting down cleanup thread.");
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("[MyContextListener] Application Started.");
    }
}