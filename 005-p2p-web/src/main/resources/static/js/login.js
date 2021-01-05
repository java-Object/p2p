var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
    try {
        if (window.opener) {
            // IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性
            referrer = window.opener.location.href;
        }
    } catch (e) {
    }
}

//按键盘Enter键即可登录
$(document).keyup(function (event) {
    if (event.keyCode == 13) {
        login();
    }
});

$(function () {
    $("#loginBtn").on("click",function () {
        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());

        $.ajax({
            //basepath：将这个资源页面的路径传过去
            url: basepath + "/user/checkLogin",
            type: "get",
            data: {
                "phone": phone,
                "loginPassword" :$.md5(loginPassword)
            },
            success: function (data) {
              if (data.code == 1){
                  //登陆成功，跳转到上一个页面
                  //获取从后台model中传递过来的url地址
                  var rUrl = $("#rUrl").val();
                  //设置登陆成功跳转的界面是上一个页面
                  window.location.href = rUrl;

              }else {
                  showError("登陆错误")
              }
            }
        });
    });
});
