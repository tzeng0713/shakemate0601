package shakemate.controller;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chatSocket/{userId}")
public class ChatWebSocket {

    // 所有已連線的用戶：userId -> Session
    private static final Map<Integer, Session> userSessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(@PathParam("userId") int userId, Session session) {
        session.setMaxTextMessageBufferSize(5 * 1024 * 1024); // 5MB
        userSessions.put(userId, session);
        System.out.println("✅ 使用者 " + userId + " 已連上 WebSocket");
    }

    @OnMessage
    public void onMessage(String message, Session senderSession, @PathParam("userId") int senderId) {
        try {
            String[] parts = message.split("\\|", 3);
            if (parts.length < 3) {
                System.err.println("⚠️ 格式錯誤，無法解析：" + message);
                return;
            }

            int roomId = Integer.parseInt(parts[0]);
            int fromId = Integer.parseInt(parts[1]);
            String content = parts[2];

            System.out.println("📩 收到訊息 from " + fromId + "：" + (content.length() > 100 ? content.substring(0, 100) + "..." : content));

            // 轉發訊息給其他用戶（可根據 roomId 過濾）
            userSessions.forEach((userId, session) -> {
                if (session.isOpen() && userId != fromId) {
                    try {
                        session.getBasicRemote().sendText(roomId + ":" + fromId + "|" + content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("❌ WebSocket 訊息處理失敗：" + e.getMessage());
        }
    }


    @OnClose
    public void onClose(@PathParam("userId") int userId, Session session) {
        userSessions.remove(userId);
        System.out.println("🔴 使用者 " + userId + " 離線");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("❌ WebSocket 錯誤：" + throwable.getMessage());
    }
}