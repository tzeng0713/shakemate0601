package com.shakemate.VO;

import java.sql.Timestamp;

public class ChatRoomVO {
	private int roomId;
	private int user1Id;
	private int user2Id;
	private int peerId;
	private String peerName;
	private String peerAvatar; // 改成 String，而不是 byte[]
	private String lastMessage;
	private Timestamp lastSentTime;
	private Timestamp createdTime;
	private boolean hasUnread;


	public ChatRoomVO() {
		super();
	}

	public ChatRoomVO(int roomId, int user1Id, int user2Id, int peerId, String peerName, String peerAvatar) {
		super();
		this.roomId = roomId;
		this.user1Id = user1Id;
		this.user2Id = user2Id;
		this.peerId = peerId;
		this.peerName = peerName;
		this.peerAvatar = peerAvatar;
	}

	public ChatRoomVO(int roomId, int user1Id, int user2Id, int peerId, String peerName, String peerAvatar,
			String lastMessage, Timestamp lastSentTime, Timestamp createdTime, boolean hasUnread) {
		super();
		this.roomId = roomId;
		this.user1Id = user1Id;
		this.user2Id = user2Id;
		this.peerId = peerId;
		this.peerName = peerName;
		this.peerAvatar = peerAvatar;
		this.lastMessage = lastMessage;
		this.lastSentTime = lastSentTime;
		this.createdTime = createdTime;
		this.hasUnread = hasUnread;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getUser1Id() {
		return user1Id;
	}

	public void setUser1Id(int user1Id) {
		this.user1Id = user1Id;
	}

	public int getUser2Id() {
		return user2Id;
	}

	public void setUser2Id(int user2Id) {
		this.user2Id = user2Id;
	}

	public int getPeerId() {
		return peerId;
	}

	public void setPeerId(int peerId) {
		this.peerId = peerId;
	}

	public String getPeerName() {
		return peerName;
	}

	public void setPeerName(String peerName) {
		this.peerName = peerName;
	}


	public String getPeerAvatar() {
		return peerAvatar;
	}


	public void setPeerAvatar(String peerAvatar) {
		this.peerAvatar = peerAvatar;
	}


	public String getLastMessage() {
		return lastMessage;
	}


	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}


	public Timestamp getLastSentTime() {
		return lastSentTime;
	}


	public void setLastSentTime(Timestamp lastSentTime) {
		this.lastSentTime = lastSentTime;
	}


	public Timestamp getCreatedTime() {
		return createdTime;
	}


	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	
}
