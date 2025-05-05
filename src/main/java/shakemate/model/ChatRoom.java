package shakemate.model;

public class ChatRoom {
	private int roomId;
	private int user1Id;
	private int user2Id;
	private int peerId;
	private String peerName;
	private String peerAvatar; // 改成 String，而不是 byte[]
	
	public ChatRoom() {
		super();
	}


	public ChatRoom(int roomId, int user1Id, int user2Id, int peerId, String peerName, String peerAvatar) {
		super();
		this.roomId = roomId;
		this.user1Id = user1Id;
		this.user2Id = user2Id;
		this.peerId = peerId;
		this.peerName = peerName;
		this.peerAvatar = peerAvatar;
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


	
}
