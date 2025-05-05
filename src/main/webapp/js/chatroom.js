	const urlParams = new URLSearchParams(window.location.search);
	const currentUserId = parseInt(urlParams.get("currentUserId")) || null;
	
	if (!currentUserId) {
		alert("⚠️ 無法取得 currentUserId，請確認網址格式是否正確！");
	}
	
	let currentRoomId = null;
	let selectedImageFile = null;
	
	function toggleSidebar() {
		const wrapper = document.querySelector('.sidebar-wrapper');
		const toggleBtn = document.getElementById('menuToggle');
		wrapper.classList.toggle('active');
		toggleBtn.classList.toggle('active');
	}
	
	// 取得聊天室清單
	function loadChatRooms() {
		fetch(`ChatRoomListServlet?currentUserId=${currentUserId}`)
			.then(res => res.json())
			.then(data => {
				const ul = document.querySelector(".chat-list");
				ul.innerHTML = "";
				//	  document.querySelector(".chat-input").style.display = "None";	 
	
				data.forEach(room => {
					const li = document.createElement("li");
					li.classList.add("chat-item");
					li.dataset.roomid = room.roomId;
					li.dataset.name = room.peerName;
					li.onclick = () => handleRoomClick(room.roomId, room.peerName, room.peerId, room.peerAvatar);
	
					li.innerHTML = `
					  <img src="data:image/jpeg;base64,${room.peerAvatar}" />
					  <div class="chat-list-txt">
					    <h3>${room.peerName}</h3>
					    <p></p>
					  </div>
					  <span class="unread-dot"></span> <!-- ✅ 不要加 style -->
					`;

					ul.appendChild(li);
				});
			})
			.catch(error => {
				console.error("載入聊天室時出錯:", error);
			});
	}
	
	// 點擊聊天室後的處理
	function handleRoomClick(roomId, peerName, peerId, peerAvatar) {
		console.log("點到聊天室了，roomId:", roomId);
		switchChat(roomId);
	
		fetch("GetRoomMessageServlet", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: `roomId=${roomId}`
		})
			.then(res => res.json())
			.then(data => {
				console.log("後端回應:", data);
				renderHistoryMessages(data, peerName, peerAvatar);
				document.querySelector(".chat-input").style.display = "flex";
				const li = document.querySelector(`.chat-list li[data-roomid="${roomId}"]`);
				if (li) {
				  const dot = li.querySelector(".unread-dot");
				  if (dot) dot.classList.remove("show"); // ✅ 改用 class 移除
				}
			})
			.catch(err => {
				console.error("取得訊息時出錯:", err);
			});
	}
	
	function switchChat(roomId) {
		currentRoomId = roomId;
		document.querySelectorAll('.chat-list li').forEach(li => {
			li.classList.remove('active');
			if (li.dataset.roomid == roomId) {
				li.classList.add('active');
			}
		});
	}
	
	// 顯示歷史訊息
	function renderHistoryMessages(data, peerName, peerAvatar) {
		const container = document.getElementById("chatContent");
		container.innerHTML = '';
	
		data.forEach(msg => {
			const isMe = msg.senderId == currentUserId;
			const el = document.createElement("div");
			el.className = "message" + (isMe ? " right" : " left");
	
			if (isMe) {
				el.innerHTML = `
	        <div class="bubble-block">
	          <div class="bubble">${msg.content}</div>
	        </div>
	      `;
			} else {
				el.innerHTML = `
	        <div class="avatar" onclick="openProfile('${peerName}', '${peerAvatar}')">
	          <div class="pic">
	            <img src="data:image/jpeg;base64,${peerAvatar}" alt="${peerName}"/>
	          </div>
	        </div>
	        <div class="bubble-block">
	          <div class="name">${peerName}</div>
	          <div class="bubble">${msg.content}</div>
	        </div>
	      `;
			}
	
			container.appendChild(el);
		});
	
		container.scrollTop = container.scrollHeight;
	}
	

	// 傳送訊息
	function sendMessage() {
		const input = document.getElementById("msgInput");
		const content = input.value.trim();
		if (!content  && !selectedImageFile) return;
	
		// 寫入資料庫
		fetch("SendMessageServlet", {
			method: "POST",
			headers: { "Content-Type": "application/x-www-form-urlencoded" },
			body: `roomId=${currentRoomId}&senderId=${currentUserId}&content=${encodeURIComponent(content)}`
		});
		
		
		// 傳送給對方
		if (socket && socket.readyState === WebSocket.OPEN) {
			socket.send(`${currentRoomId}|${currentUserId}|${content}`);
		}
	
		// 即時顯示在畫面上
		const container = document.getElementById("chatContent");
		const el = document.createElement("div");
		el.className = "message right";
		el.innerHTML = `
	    <div class="bubble-block">
	      <div class="bubble">${content}</div>
	    </div>
	  `;
		container.appendChild(el);
		container.scrollTop = container.scrollHeight;
	
		input.value = "";
		
		// ✅ 更新左側聊天室預覽
		const li = document.querySelector(`.chat-list li[data-roomid="${currentRoomId}"]`);
		if (li) {
			const preview = li.querySelector("p");
			preview.textContent = content.length > 20 ? content.substring(0, 20) + "..." : content;

			// ✅ 也移到最上方（你自己講話也應該觸發這邏輯）
			document.querySelector(".chat-list").prepend(li);
			li.classList.remove("fade-in");
			void li.offsetWidth;
			li.classList.add("fade-in");
		}
	}
	
	// 接收到即時訊息
	function renderIncomingMessage(senderId, content = "", imageBase64 = null) {
	  const isMe = senderId === currentUserId;
	  const container = document.getElementById("chatContent");
	  const el = document.createElement("div");
	  el.className = "message" + (isMe ? " right" : " left");

	  let messageContent = "";
	  if (imageBase64) {
	    messageContent = `<img src="${imageBase64}" style="max-width: 200px; border-radius: 8px;" />`;
	  } else {
	    messageContent = content;
	  }

	  if (isMe) {
	    el.innerHTML = `
	      <div class="bubble-block">
	        <div class="bubble">${messageContent}</div>
	      </div>
	    `;
	  } else {
	    const li = document.querySelector(`.chat-list li[data-roomid="${currentRoomId}"]`);
	    const peerName = li?.dataset.name || "對方";
	    const peerImgSrc = li?.querySelector("img")?.src || "";

	    el.innerHTML = `
	      <div class="avatar" onclick="openProfile('${peerName}', '${peerImgSrc}')">
	        <div class="pic">
	          <img src="${peerImgSrc}" alt="${peerName}"/>
	        </div>
	      </div>
	      <div class="bubble-block">
	        <div class="name">${peerName}</div>
	        <div class="bubble">${messageContent}</div>
	      </div>
	    `;
	  }

	  container.appendChild(el);
	  container.scrollTop = container.scrollHeight;
	}

	
	
	// 彈出視窗：顯示對方資訊
	function openProfile(name, avatarSrc) {
		const popupAvatar = document.getElementById("popupAvatar");
	
		// ✅ 如果已經是 base64 或完整路徑，直接用
		if (avatarSrc.startsWith("data:image")) {
			popupAvatar.src = avatarSrc;
		} else if (avatarSrc.startsWith("http")) {
			popupAvatar.src = avatarSrc;
		} else {
			// ✅ 否則假設是純 base64 編碼（不含 data:image）
			popupAvatar.src = `data:image/jpeg;base64,${avatarSrc}`;
		}
	
		document.getElementById("popupName").textContent = name;
		document.getElementById("popupIntro").textContent = `${name} 是一位可愛的夥伴，幹你娘！`;
		document.getElementById("profilePopup").style.display = "flex";
	}
	
	
	function closeProfilePopup() {
		document.getElementById("profilePopup").style.display = "none";
	}
	
	// WebSocket
	let socket = null;
	
	function connectWebSocket(userId) {
		socket = new WebSocket(`ws://localhost:8081/ShakeMateMatchTest/chatSocket/${userId}`);
	
		socket.onopen = () => {
			console.log("✅ WebSocket 已連線");
		};
	
		socket.onmessage = (event) => {
			const msg = event.data;
			const [roomInfo, contentRaw] = msg.split("|");
			const [roomId, senderId] = roomInfo.split(":").map(Number);

			const li = document.querySelector(`.chat-list li[data-roomid="${roomId}"]`);
			if (li) {
			  // 更新 preview 文本
			  const preview = li.querySelector("p");
			  preview.textContent = contentRaw.startsWith("image:") ? "[圖片]" : contentRaw.slice(0, 20) + (contentRaw.length > 20 ? "..." : "");

			  // ✅ 如果這不是目前開啟的聊天室 → 顯示紅點
			  if (parseInt(roomId) !== currentRoomId) {
			  	const dot = li.querySelector(".unread-dot");
			  	if (dot) {
			  		dot.classList.add("show");
			  		dot.style.display = ""; // ✅ 清除任何舊的 inline style
			  	}
			  }


			  // 移動 + 動畫
			  document.querySelector(".chat-list").prepend(li);
			  li.classList.remove("fade-in");
			  void li.offsetWidth;
			  li.classList.add("fade-in");
			}

			// ✅ 如果剛好正在看這個聊天室，就顯示訊息氣泡
			if (parseInt(roomId) === currentRoomId) {
				if (contentRaw.startsWith("image:")) {
					const encoded = contentRaw.substring(6);
					const imageBase64 = decodeURIComponent(encoded);
					renderIncomingMessage(senderId, "", imageBase64);
				} else {
					renderIncomingMessage(senderId, contentRaw);
				}
			}
		};


	
		socket.onclose = () => {
			console.log("🔴 WebSocket 已關閉");
		};
	
		socket.onerror = (error) => {
			console.error("❌ WebSocket 發生錯誤：", error);
		};
	}
	
	// 初始事件綁定
	document.addEventListener('DOMContentLoaded', () => {
		document.querySelector(".chat-input").style.display = "None";
		loadChatRooms();
	
		const input = document.getElementById("msgInput");
		input.addEventListener('keydown', (event) => {
			if (event.key === 'Enter') {
				event.preventDefault();
				sendMessage();
			}
		});
	
		connectWebSocket(currentUserId);
	});

	// 綁定點擊事件，點圖示時觸發檔案選擇
	document.getElementById("uploadImageBtn").addEventListener("click", function () {
	  document.getElementById("imageInput").click();
	});

	// 當選擇圖片後
	document.getElementById("imageInput").addEventListener("change", function (event) {
		const file = event.target.files[0];
		if (!file) return;

		const reader = new FileReader();
		reader.onload = function (e) {
		  const imageBase64 = e.target.result;

		  // 1. 顯示在自己視窗
		  const container = document.getElementById("chatContent");
		  const el = document.createElement("div");
		  el.className = "message right";
		  el.innerHTML = `
		    <div class="bubble-block">
		      <div class="bubble"><img src="${imageBase64}" style="max-width: 200px; border-radius: 8px;" /></div>
		    </div>
		  `;
		  container.appendChild(el);
		  container.scrollTop = container.scrollHeight;

		  // 2. 傳送給後端（如果你有要儲存可加上 fetch）
		  const formData = new FormData();
		  formData.append("roomId", currentRoomId);
		  formData.append("senderId", currentUserId);
		  formData.append("content", null); // 如果是純圖片，可留空或補上說明文字
		  formData.append("img", imageBase64); // <input type="file"> 選到的圖片檔案

		  fetch("SendMessageServlet", {
		    method: "POST",
		    body: formData, // 自動帶 multipart/form-data
		  });

		  

		  // 3. 用 WebSocket 傳送圖片（加上標示 image: 前綴）
		  if (socket && socket.readyState === WebSocket.OPEN) {
			const encodedImage = encodeURIComponent(imageBase64);
			socket.send(`${currentRoomId}|${currentUserId}|image:${encodedImage}`);
		  }
		};

		reader.readAsDataURL(file);
	});

