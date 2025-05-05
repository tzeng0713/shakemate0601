	let currentUser = "油條";


	function sendMatch(action, targetId) {
      fetch("MatchServlet", {
        method: "POST", 
        headers: {
        	"Content-Type": "application/x-www-form-urlencoded",
        },
		body: `action=${action}&targetId=${targetId}`
      })
        .then((res) => res.text())
        .then((data) => {
          console.log("後端有回應喔！");
        })
        .catch((err) => {
          console.error("錯誤發生：", err);
        });
    }
	
	const swiper = new Swiper('.swiper', {
	    loop: false,
	    slidesPerView: 1, // 一次只顯示一張卡片 ✅
	    spaceBetween: 0,  // 卡片之間沒有空隙 ✅
	    centeredSlides: false, // 不需要置中（讓每張緊貼）✅
	    pagination: {
	      el: '.swiper-pagination',
	      clickable: true
	    }
	  });
	  