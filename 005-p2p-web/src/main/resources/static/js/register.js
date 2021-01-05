//错误提示
function showError(id, msg) {
    $("#" + id + "Ok").hide();
    $("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
    $("#" + id + "Err").show();
    $("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id + "Ok").show();
    $("#" + id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });

    //手机号验证
    $("#phone").on("blur", function () {
        // alert("111")		检验请求是否到这里
        var phone = $.trim($("#phone").val());	// $.trim()：去空格
        if (phone == "") {
            showError("phone", "对不起，手机号不能为空");
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            //验证手机号格式
            showError("phone", "对不起，您的手机号格式不正确")
        } else {
            //异步向数据库发出请求，查看该手机号是否被注册
            $.ajax({
                //发送请求，接收数据，返回json
                url: "/user/checkPhone",
                type: "get",
                data: "phone=" + phone,
                success: function (data) {
                    if (data.code == "1") {
                        showSuccess("phone");
                    } else {
                        showError("phone", "对不起，该手机号已被注册！");
                    }
                },
                error: function () {
                    //执行到这里，说明请求都没有过去！
                    showError("phone", "系统繁忙！请稍后再试");
                }
            });
        }
    });


    //验证密码格式是否正确
    $("#loginPassword").on("blur", function () {
        var loginPassword = $.trim($("#loginPassword").val());

        //非空验证
        if (loginPassword == "") {
            showError("loginPassword", "登录密码不能为空！");
        }
        //密码的长度必须在6-12位之间
        else if (loginPassword.length < 6 || loginPassword.length > 12) {
            showError("loginPassword", "密码的长度必须在6-12位之间");
        }
        //密码字符只可使用数字和大小写英文字母
        else if (!/^[0-9a-zA-Z]+$/.test(loginPassword)) {
            showError("loginPassword", "密码字符只可使用数字和大小写英文字母");
        }
        //密码应同时包含英文和数字
        else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)) {
            showError("loginPassword", "密码应同时包含英文和数字");
        }
        //验证通过
        else {
            showSuccess("loginPassword");
        }
    });


    // 注册验证码输入框失去焦点事件
    $("#messageCode").on("blur",function () {

        //获取注册验证码输入框的值，并且去空格
        var messageCode = $.trim($("#messageCode").val());

        //非空验证
        if (messageCode == ""){
            showError("messageCode","验证码不能为空哦！")
        } else{
            showSuccess("messageCode");
        }

    });

    //点击获取验证码按钮
    $("#messageCodeBtn").on("click",function () {
        //重新验证一下账号密码
        $("#phone").blur();
        $("#loginPassword").blur();
        //获取去空格之后的电话号码，准备传入后台比对
        var phone = $.trim($("#phone").val());
        // alert(phone)
        // $选择器：将div中所有id属性中带有Err的字段触发,就是查看页面还有没有错误提示
        var errorText = $("div[id$='Err']").text();
        //当账号跟密码没有错误时才执行下面
        if ( errorText == ""){
            $.ajax({
                //请求后台查看验证码是否发送成功！
                url: "/user/messageCode",
                type: "get",
                data: {
                    "phone":phone
                },
                success: function (data) {

                    //发送成功
                    if (data.code == 1){

                        //注：测试使用，弹出验证码
                        alert(data.messageCode);

                        // hasClass：检查当前的元素中是否含有当前的类，有就返回true
                        //就是有没有点击这个对象，有就执行，当前是取反，所以当前属性没有改变，就走下面进行倒计时
                        if ( !$("#messageCodeBtn").hasClass("on") ){
                            //使用倒计时插件
                            $.leftTime(60,function (d) {
                                if ( d.status ){
                                    // 为 on 加入 messageCodeBtn 对象 把按钮设置为不可用
                                    $("#messageCodeBtn").addClass("on");
                                    $("#messageCodeBtn").html(d.s == "00" ? "60秒后获取" : d.s+"秒后获取");
                                }else {
                                    //倒计时结束
                                    //删除 on 中的对象 修改按钮样式，设置为可用
                                    $("#messageCodeBtn").removeClass("on");
                                    $("#messageCodeBtn").html("获取验证码");
                                }
                            })
                        }
                    }else {
                        //发送失败
                        showError("messageCode",data.message)
                    }
                },
                error: function () {
                    //请求失败
                    showError("messageCode","业务繁忙，请稍后重试！")
                }
            });
        }
    })


    //注册按钮单击事件
    $("#btnRegist").on("click", function () {

        //触发一下事件，重新验证账号密码
        $("#phone").blur();
        $("#loginPassword").blur();
        $("#messageCode").blur();

        //获取输入框的值，并且去空格
        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var messageCode = $.trim($("#messageCode").val());

        //给密码加密
        $("#loginPassword").val($.md5(loginPassword));
        //  $选择器：将div中所有id属性中带有Err的字段触发,就是查看页面还有没有错误提示
        var errorText = $("div[id$='Err']").text();
        //没有错误提示的话就走这里,没有报错就可以执行了
        if (errorText == "") {
            $.ajax({
                url: "/user/register",
                type: "post",
                data: {
                    "phone": phone,
                    "loginPassword": $.md5(loginPassword),
                    "messageCode" : messageCode
                },
                success: function (data) {
                    if (data.code == 1) {
                        //跳转到首页
                        window.location.href = "/index";
                    } else {
                        //注册失败
                        showError("messageCode", data.message);
                    }
                },
                error: function () {
                    showError("messageCode", "系统繁忙！");
                }
            });
        }
    });

});



