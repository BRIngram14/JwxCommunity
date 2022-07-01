$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	//图标蓝色的时候表示可以点关注 灰色表示点击会取消关注
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
			CONTEXT_PATH+"/follow",
			//取profile.html中调该方法上一行中手动获得的userId
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data){
				data=$.parseJSON(data);
				if(data.code==0)
				{
					window.location.reload();
				}
				else{
					alert(data.msg);
				}
			}
		)
		//$(btn).text("已关注").removeClass("btn-info").addClass("btn-secondary");
	} else {
		// 取消关注
		$.post(
			//$(btn).text("关注TA").removeClass("btn-secondary").addClass("btn-info");
			CONTEXT_PATH + "/unfollow",
			{"entityType": 3, "entityId": $(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if (data.code == 0) {
					window.location.reload();
				} else {
					alert(data.msg);
				}
			}
		);
	}
}