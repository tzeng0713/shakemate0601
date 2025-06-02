const urlParams = new URLSearchParams(window.location.search);
const currentUserId = parseInt(urlParams.get("currentUserId")) || null;
const roomId = urlParams.get("roomId") || null;

if (!currentUserId) {
	alert("⚠️ 無法取得 currentUserId，請確認網址格式是否正確！");
}


let currentTargetId = null;
// 條件篩選按鈕
const filterBtn = document.querySelector('.filterBtn');
// 條件篩選彈出視窗
const filterModal = document.getElementById('filterModal');
// 套用篩選按鈕
const applyFilterBtn = document.querySelector('.btn-primary');

let matchList = [];
let currentIndex = 0;

// ✅ 頁面一開始，檢查是否有 localStorage 暫存的 matchList（來自 matchSuccess.html）
const savedList = localStorage.getItem(`matchedList_${currentUserId}`);
const savedFilters = localStorage.getItem(`matchFilters_${currentUserId}`);

if (savedList && savedFilters) {
	matchList = JSON.parse(savedList);
	currentIndex = 0;

	if (matchList.length > 0) {
		renderMatchCard(matchList[currentIndex]);
	} else {
		// 預設隨機推薦（沒有條件篩選）
		fetch(`MatchControllerServlet?action=getNext&currentUserId=${currentUserId}`)
			.then(res => res.json())
			.then(profile => {
				renderMatchCard(profile);
			})
			.catch(err => {
				console.error("初始化會員資料失敗", err);
			});
	}

	// ❌ 不清除 localStorage，這次要留下來繼續滑！
} else {
	// 預設隨機推薦（沒有條件篩選）
	fetch(`MatchControllerServlet?action=getNext&currentUserId=${currentUserId}`)
		.then(res => res.json())
		.then(profile => {
			renderMatchCard(profile);
		})
		.catch(err => {
			console.error("初始化會員資料失敗", err);
		});
}



// 點擊「條件篩選」打開視窗
filterBtn.addEventListener('click', () => {
	filterModal.style.display = 'flex';  // 原本 CSS 應為 display: none
});

// 條件篩選視窗 按下確認套用的按鈕
applyFilterBtn.addEventListener('click', () => {
	// 取得性別
	const gender = document.getElementById('genderSelect').value;

	// 取得勾選的興趣 (class: interest)
	const interests = Array.from(document.querySelectorAll('.interest:checked'))
		.map(input => input.value);

	// 取得勾選的人格特質 (class: personality)
	const personality = Array.from(document.querySelectorAll('.personality:checked'))
		.map(input => input.value);

	console.log('✅ 篩選資料送出：', { gender, interests, personality });
	// 送出後關掉視窗
	filterModal.style.display = 'none';


	// 👉 準備 payload（送出的資料）
	const payload = {
		action: "getFiltered",
		currentUserId: currentUserId, // 從你的 URL 或變數取得
		gender: gender,
		interests: interests,
		personality: personality
	};

	// 👉 發送 POST 請求
	fetch("MatchControllerServlet", {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify(payload),
	})
		.then(res => {
			if (!res.ok) throw new Error("伺服器回應失敗");
			return res.json();
		})
		.then(data => {
			console.log("🎯 篩選結果：", data);
			matchList = data;
			currentIndex = 0;
			renderMatchCard(matchList[currentIndex]);

			// ✅ 儲存篩選資料與結果
			localStorage.setItem(`matchedList_${currentUserId}`, JSON.stringify(matchList));
			localStorage.setItem(`matchFilters_${currentUserId}`, JSON.stringify({
				gender,
				interests,
				personality
			}));
		})
		.catch(err => {
			console.error("❌ 發送失敗：", err);
		});


});

// 點擊視窗外區域或取消按鈕會關閉篩選彈窗
filterModal.addEventListener("click", function(event) {
	const isInsideModal = event.target.closest(".modal-content");
	const isCancelButton = event.target.classList.contains("btn-secondary");

	if (!isInsideModal || isCancelButton) {
		filterModal.style.display = "none";
	}
});


// 渲染會員卡片畫面
function renderMatchCard(profile) {
	currentTargetId = profile.userId;

	const card = document.querySelector(".match__card");
	const swiperWrapper = card.querySelector(".swiper-wrapper");

	// ✅ 銷毀舊 Swiper（如有）
	const oldSwiper = card.querySelector(".avatar-swiper")?.swiper;
	if (oldSwiper) {
		oldSwiper.destroy(true, true);
	}

	// ✅ 清空 wrapper
	while (swiperWrapper.firstChild) {
		swiperWrapper.removeChild(swiperWrapper.firstChild);
	}

	// ✅ 建立內層卡片 DOM
	const slide = document.createElement("div");
	slide.className = "swiper-slide";

	const body = document.createElement("div");
	body.className = "match__body";

	const avatarBox = document.createElement("div");
	avatarBox.className = "match__avatar-box";

	const swiperContainer = document.createElement("div");
	swiperContainer.className = "swiper avatar-swiper";

	const avatarWrapper = document.createElement("div");
	avatarWrapper.className = "swiper-wrapper";

	// ✅ 建立頭貼輪播
	profile.avatarList.forEach(url => {
		const avatarSlide = document.createElement("div");
		avatarSlide.className = "swiper-slide";

		const img = document.createElement("img");
		img.className = "match__avatar";
		img.src = url;

		avatarSlide.appendChild(img);
		avatarWrapper.appendChild(avatarSlide);
	});

	const avatarPagination = document.createElement("div");
	avatarPagination.className = "swiper-pagination";

	swiperContainer.appendChild(avatarWrapper);
	swiperContainer.appendChild(avatarPagination);
	avatarBox.appendChild(swiperContainer);

	// ✅ 建立個人資料欄位
	const detailBox = document.createElement("div");
	detailBox.className = "match__details";

	const fields = [
		{ title: "人格特質", content: profile.personality },
		{ title: "興趣專長", content: profile.interests },
		{ title: "個人簡介", content: profile.intro },
	];

	fields.forEach(f => {
		const field = document.createElement("div");
		field.className = "match__field";

		const h3 = document.createElement("h3");
		h3.className = "match__field-title";
		h3.textContent = f.title;

		const p = document.createElement("p");
		p.className = "match__field-content";
		p.textContent = f.content;

		field.appendChild(h3);
		field.appendChild(p);
		detailBox.appendChild(field);
	});

	body.appendChild(avatarBox);
	body.appendChild(detailBox);
	slide.appendChild(body);
	swiperWrapper.appendChild(slide);

	// ✅ 名稱、年齡星座
	document.querySelector(".match__name").textContent = profile.username;
	document.querySelector(".match__info").textContent = `${profile.age}歲・${profile.zodiac}`;

	// ✅ 初始化 Swiper
	new Swiper(".avatar-swiper", {
		pagination: {
			el: ".avatar-swiper .swiper-pagination",
			clickable: true,
		},
		loop: true,
	});
}


//// 監聽動畫結束事件 → fade-out 結束後 fetch 資料 + render + fade-in
//document.querySelector(".match__wrap").addEventListener("animationend", (e) => {
//	console.log("🎬 動畫結束事件成功觸發");
//	const card = e.target;
//	const wrap = document.querySelector(".match__wrap");
//
//	if (wrap.classList.contains("fade-out")) {
//		wrap.classList.remove("fade-out");
//		wrap.style.visibility = "hidden";
//
//		if (matchList.length > 0) {
//			renderMatchCard(matchList[currentIndex]);
//			wrap.style.visibility = "visible";
//			wrap.classList.add("fade-in");
//		} else {
//			// ❌ matchList 沒資料 → 改從後端撈
//			fetch(`MatchControllerServlet?action=getNext&currentUserId=${currentUserId}`)
//				.then(res => res.json())
//				.then(profile => {
//					if (!profile || !profile.userId) {
//						wrap.style.visibility = "hidden";
//						throw new Error("後端沒資料");
//					}
//					renderMatchCard(profile);
//					wrap.style.visibility = "visible";
//					wrap.classList.add("fade-in");
//				})
//				.catch(err => {
//					console.error("❌ 沒有更多會員了", err);
//					alert("你已經看完所有人啦！");
//				});
//		}
//	}
//
//	if (wrap.classList.contains("fade-in")) {
//		wrap.classList.remove("fade-in"); // 清除動畫 class
//	}
//});


// 這段 JS 專門處理按下 like 或 dislike 按鈕後的行為
const likeBtn = document.querySelector(".match__button--like");
const dislikeBtn = document.querySelector(".match__button--dislike");

// 綁定按鈕點擊事件
likeBtn.addEventListener("click", () => sendMatch("like"));
dislikeBtn.addEventListener("click", () => sendMatch("dislike"));

// 這個函式會由前端頁面在 renderMatchCard() 時設定 currentTargetId
function setCurrentTargetId(id) {
	currentTargetId = id;
}

function sendMatch(action) {
	fetch("MatchControllerServlet", {
		method: "POST",
		headers: {
			"Content-Type": "application/x-www-form-urlencoded",
		},
		body: `action=${action}&targetId=${currentTargetId}&currentUserId=${currentUserId}`,
	})
		.then((res) => res.json())
		.then((data) => {
			const wrap = document.querySelector(".match__wrap");
			wrap.classList.remove("fade-in", "fade-out");
			void wrap.offsetWidth;
			wrap.classList.add("fade-out");

			if (action === "like") {
				if (data.alreadyActed) {
					alert("⚠️ 你已經按過這個人囉");
				} else if (data.matched) {
					// ✅ 如果是配對成功 → 把目前這筆從 matchList 移除
					matchList.splice(currentIndex, 1);
					localStorage.setItem(`matchedList_${currentUserId}`, JSON.stringify(matchList));
					// 對方也按過你：跳轉成功配對頁面
					window.location.href = `matchSuccess.html?roomId=${data.roomId}&currentUserId=${currentUserId}`;
					return; // 不切換下一位，直接跳頁
				}
			}

			// 👉 like 或 dislike：都要換下一張（除非成功配對已跳頁）
			// ✅ 移除當前這位使用者
			matchList.splice(currentIndex, 1);
			localStorage.setItem(`matchedList_${currentUserId}`, JSON.stringify(matchList));

			if (matchList.length > 0) {
				// ✅ 有剩下的卡片：從 matchList 顯示下一張
				renderMatchCard(matchList[currentIndex]);
				wrap.classList.remove("fade-out");
				void wrap.offsetWidth;
				wrap.classList.add("fade-in");
			} else {
				// 從後端抓下一位（若是隨機推薦的情況）
				fetch(`MatchControllerServlet?action=getNext&currentUserId=${currentUserId}`)
					.then(res => res.json())
					.then(profile => {
						if (!profile || !profile.userId) {
							// 🧼 補充：避免殘影，清空畫面
							document.querySelector(".match__wrap").style.visibility = "hidden";
							throw new Error("後端沒資料");
						}
						// 正常情況
						renderMatchCard(profile);
						wrap.classList.remove("fade-out");
						void wrap.offsetWidth;
						wrap.classList.add("fade-in");
					})
					.catch(err => {
						console.error("❌ 沒有更多會員了", err);
						alert("你已經看完所有人啦！");
					});
			}
		})
}

function goToChat() {
	if (!roomId || !currentUserId) {
		alert("❌ 缺少參數，無法導向聊天室！");
		return;
	}
	window.location.href = `chatroom.html?currentUserId=${currentUserId}&currentRoomId=${roomId}`;
}
// 處理配對成功提示頁面中，點擊「再抖一下」按鈕的行為
function goToMatch() {
	if (!currentUserId) {
		alert("⚠️ 無法取得 currentUserId，請確認網址格式是否正確！");
		return;
	}

	// ✅ 不清除 localStorage，直接跳轉回配對頁
	window.location.href = `match2.html?currentUserId=${currentUserId}&fromSuccess=1`;
}
