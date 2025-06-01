package com.shakemate.VO;

public class ChatMessageVO {
	private int messageId;
	private int roomId;
	private int senderId;
	private String content;
	private byte[] imgBytes;
	private String sentTime;
	private boolean isRead; 
	private String imgBase64;
	
	public ChatMessageVO() {
		super();
	}

	public ChatMessageVO(int messageId, int roomId, int senderId, String content, String imgBase64, String sentTime, boolean isRead) {
		super();
		this.messageId = messageId;
		this.roomId = roomId;
		this.senderId = senderId;
		this.content = content;
		this.imgBase64 = imgBase64;
		this.sentTime = sentTime;
		this.isRead = isRead;
	}
	
	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSentTime() {
		return sentTime;
	}

	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}


	public byte[] getImgBytes() {
		return imgBytes;
	}

	public void setImgBytes(byte[] imgBytes) {
		this.imgBytes = imgBytes;
	}

	public boolean isRead() {
		return isRead;
	}


	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public String getImgBase64() {
		return imgBase64;
	}

	public void setImgBase64(String imgBase64) {
		this.imgBase64 = imgBase64;
	}
	
	
}
