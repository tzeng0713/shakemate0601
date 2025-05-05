package shakemate.model;

public class ChatMessage {
	private int messageId;
	private int roomId;
	private int senderId;
	private String content;
	private String sentTime;
	private String imgBase64;
	
	public ChatMessage() {
		super();
	}

	public ChatMessage(int messageId, int roomId, int senderId, String content, String sentTime, String imgBase64) {
		super();
		this.messageId = messageId;
		this.roomId = roomId;
		this.senderId = senderId;
		this.content = content;
		this.sentTime = sentTime;
		this.imgBase64 = imgBase64;
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

	public String getImgBase64() {
		return imgBase64;
	}

	public void setImgBase64(String imgBase64) {
		this.imgBase64 = imgBase64;
	}

}
