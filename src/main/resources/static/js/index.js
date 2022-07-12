$(function(){
	//点击后触发publish方法
	$("#publishBtn").click(publish);

});

function publish() {
	//发布后隐藏发布的框
	$("#publishModal").modal("hide");

	//发送AJAX请求之前 将CSRF令牌设置到请求的消息头中
	//取到name等于csrf的meta元素的content属性值
	// var token=$("meta[name='_csrf']").attr("content");
	// var header=$("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function (e,xhr,options){
	// 	xhr.setRequestHeader(header,token);
	// });

	//获取标题和内容
	var title =$("#recipient-name").val();//标题的id
	var content=$("#message-text").val();
	//发送异步请求(post)
	$.post(
		//访问路径
		CONTEXT_PATH+"/discuss/add",
		//提交的数据
		{"title":title,"content":content},
		//浏览器得到服务器响应后，会调这个方法，把返回的数据放到data中(字符串类型)
		function (data){
			//将字符串类型data转换为json类型
			data=$.parseJSON(data);
			//在提示框中显示返回消息 hintBody是提示框的id
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			//2秒后 自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 成功发布就刷新页面
				if(data.code==0)
				{
					window.location.reload();
				}
			}, 2000);
		}
	)


}