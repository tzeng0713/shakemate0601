const urlParams = new URLSearchParams(window.location.search);
const currentUserId = parseInt(urlParams.get("currentUserId")) || null;

if (!currentUserId) {
	alert("⚠️ 無法取得 currentUserId，請確認網址格式是否正確！");
}


let currentTargetId = null;

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


// 監聽動畫結束事件 → fade-out 結束後 fetch 資料 + render + fade-in
document.querySelector(".match__wrap").addEventListener("animationend", (e) => {
	console.log("🎬 動畫結束事件成功觸發");
	const card = e.target;
	const wrap = document.querySelector(".match__wrap");
	
	if (wrap.classList.contains("fade-out")) {
		wrap.classList.remove("fade-out");

		// 🧼 立刻隱藏舊卡片，避免舊卡閃一下
		wrap.style.visibility = "hidden";

		fetch(`MatchControllerServlet?action=getNext&currentUserId=${currentUserId}`)
			.then(res => {
				if (!res.ok) throw new Error("查無資料");
				return res.json();
			})
			.then(profile => {
				renderMatchCard(profile);      // ✅ 更新 DOM
				wrap.style.visibility = "visible";  // ✅ 顯示新卡
				wrap.classList.add("fade-in"); // ✅ 播放進場動畫
			})
			.catch(err => {
				document.querySelector(".match__actions").style.visibility = "hidden";
//				header.style.visibility = "hidden";
				console.error("❌ 沒有更多會員了", err);
				alert("你已經看完所有人啦！");
			});
	}

	if (wrap.classList.contains("fade-in")) {
		wrap.classList.remove("fade-in"); // 清除動畫 class
	}
});

// 初始化頁面 → 撈第一筆資料
fetch(`MatchControllerServlet?action=getNext&currentUserId=${currentUserId}`)
	.then(res => res.json())
	.then(profile => {
		renderMatchCard(profile);
	})
	.catch(err => {
		console.error("初始化會員資料失敗", err);
	});


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

// 發送配對請求
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
			if (action === "like") {
				if (data.alreadyActed) {
					alert("⚠️ 你已經按過這個人囉");
				} else if (data.matched) {
					// 成功配對！導向成功頁面（可以帶 roomId 當參數）
					window.location.href = `matchSuccess.html?roomId=${data.roomId}&currentUserId=${currentUserId}`;
				} else {
					// 對方還沒按你 → 繼續下一位（可手動觸發動畫或 reload 卡片）
//					document.querySelector(".match__wrap").classList.add("fade-out");
					// 👉 播動畫
					const wrap = document.querySelector(".match__wrap");
					wrap.classList.remove("fade-in", "fade-out");
					void wrap.offsetWidth;
					wrap.classList.add("fade-out");
				}
			} else if (action === "dislike") {
				// 不喜歡也一樣換下一位
//				document.querySelector(".match__wrap").classList.add("fade-out");
				// 👉 播動畫
				const wrap = document.querySelector(".match__wrap");
				wrap.classList.remove("fade-in", "fade-out");
				void wrap.offsetWidth;
				wrap.classList.add("fade-out");
			}
		})
		.catch((err) => {
			console.error("❌ 傳送配對失敗", err);
			alert("伺服器錯誤，請稍後再試！");
		});
}