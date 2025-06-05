package com.shakemate.WebSocket.controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.websocket.server.ServerContainer;
import com.shakemate.WebSocket.controller.*;

@WebListener
public class WebSocketConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            ServerContainer container = (ServerContainer)
                    sce.getServletContext().getAttribute("jakarta.websocket.server.ServerContainer");

            container.addEndpoint(ChatWebSocket.class);
            System.out.println("✅ ChatWebSocket 已成功註冊");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}