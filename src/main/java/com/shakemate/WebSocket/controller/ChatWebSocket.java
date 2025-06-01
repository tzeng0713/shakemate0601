package com.shakemate.WebSocket.controller;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
			System.out.println("HI");
			// 如果是 JSON 結構（已讀通知）
			if (message.trim().startsWith("{")) {
				JsonObject json = JsonParser.parseString(message).getAsJsonObject();
				if ("read".equals(json.get("type").getAsString())) {
					int roomId = json.get("roomId").getAsInt();
					int readerId = json.get("readerId").getAsInt(); // 其實可以直接用 senderId，但你要保留我就不動它
					int messageSenderId = json.get("senderId").getAsInt(); // 原始訊息的 sender 才是被通知的對象

					System.out.println("👁️‍🗨️ user " + readerId + " 已讀聊天室 " + roomId + " 訊息");

					Session receiverSession = userSessions.get(messageSenderId);
					if (receiverSession != null && receiverSession.isOpen()) {
						JsonObject notify = new JsonObject();
						notify.addProperty("type", "read");
						notify.addProperty("roomId", roomId);
						notify.addProperty("readerId", readerId); // 告訴對方誰已讀

						receiverSession.getBasicRemote().sendText(notify.toString());
					}
					return; // ⚠️ 處理完 read 就 return，不要繼續跑下面發訊息流程
				}
			}

			String[] parts = message.split("\\|", 3);
			if (parts.length < 3) {
				System.err.println("⚠️ 格式錯誤，無法解析：" + message);
				return;
			}

			int roomId = Integer.parseInt(parts[0]);
			int fromId = Integer.parseInt(parts[1]);
			String content = parts[2];

			System.out.println("📩 收到訊息 from " + fromId + "："
					+ (content.length() > 100 ? content.substring(0, 100) + "..." : content));

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