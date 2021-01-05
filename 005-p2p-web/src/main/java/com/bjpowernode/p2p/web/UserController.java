package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.common.utils.HttpClientUtils;
import com.bjpowernode.p2p.common.utils.ResultMap;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.RedisService;
import com.bjpowernode.p2p.service.loan.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Luo
 * @date 2020/12/21 15:31
 * @Package com.bjpowernode.p2p.web
 * class 查询用户
 */

@SuppressWarnings("AlibabaRemoveCommentedCode")
@Controller
@RequestMapping("user")
public class UserController {


    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false,timeout = 15000)
    private RedisService redisService;

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false,timeout = 15000)
    private FinanceAccountService financeAccountService;

    /**
     * 查看页面上的手机号是否有被注册过
     *
     * @param phone 手机号码
     * @return 1：代表没有注册，验证通过。
     * -1：代表被注册过，此手机号注册失败。
     */
    @RequestMapping("/checkPhone")
    public @ResponseBody
    Map<String, Object> toCheckPhone(String phone) {
        Map<String, Object> jsonMap = new HashMap<>();

        //调用业务层，查询手机号是否被注册过
        User user = userService.queryUserByPhone(phone);

        //判断
        if (ObjectUtils.allNotNull(user)) {
            return ResultMap.resultError("手机号:" + phone + "已被注册！");
        }


        return ResultMap.resultSuccess();
    }


    /**
     * 功能描述: 注册新用户
     * @Param: [model, phone, loginPassword]
     * @Return: java.lang.Object
     * @Date: 2020/12/21 20:47
     */
    @RequestMapping("/register")
    public @ResponseBody
    Object toRegister(HttpServletRequest request ,Model model, String phone, String loginPassword, String messageCode) {

        try {
            //在创建用户之前，需要验证一下验证码是否正确
            String redisCode = (String) redisService.get(phone);
            if ( !StringUtils.equals(messageCode,redisCode) ){
                return ResultMap.resultError("验证码有误！");
            }

            User user = userService.queryRegister(phone, loginPassword);
//            model.addAttribute("user", user);
            //将新建的用户保存到session中
            request.getSession().setAttribute(Constants.SESSION_USER,user);
        }catch (Exception e){
            e.printStackTrace();
            return ResultMap.resultError("系统繁忙");
        }

        return ResultMap.resultSuccess();
    }


    /**
     * 功能描述: 检查验证码是否发送成功，并且将验证码存到redis数据库
     * @Param: [phone] 需要发送验证码的手机号
     * @Return: java.lang.Object
     * @Date: 2020/12/22 21:34
     */
    @RequestMapping("/messageCode")
    public @ResponseBody Object toMessageCode(String phone){

        String code = null;

        try {

            //请求京东万象发送短信接口 https://way.jd.com/kaixintong/kaixintong
            //短信的接口地址
            String url = "https://way.jd.com/kaixintong/kaixintong";

            Map<String,Object> paramMap = new HashMap<>();
            //密钥：从接口那里获取
            paramMap.put("appkey","10da2835ea753d7a4c40cec96b5d1a58");
            //手机号：发送短信的手机号
            paramMap.put("mobile",phone);
            //生成6位随机数
            code = this.getRandomCode(6);
            System.out.println("验证码是："+ code);
            //将要发送的内容存入集合
            paramMap.put("content","【凯信通】您的验证码是：" + code);

            //从京东万象获取验证码
            //String jsonPost = HttpClientUtils.doPost(url, paramMap);

            //手动获取验证码，测试使用
            String jsonPost = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 0,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-7047336</remainpoint>\\n <taskID>166072786</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                    "}";


            JSONObject jsonObject = JSONObject.parseObject(jsonPost);
            String resultCode = (String)jsonObject.get("code");

            if ( !StringUtils.equals(resultCode,"10000") ){
                return ResultMap.resultError("通信异常");
            }

            String xmlString = (String)jsonObject.get("result");
            Document document = DocumentHelper.parseText(xmlString);
            Node node = document.selectSingleNode("returnsms/returnstatus[1]");

            String nodeText = node.getText();

            if ( !StringUtils.equals(nodeText,"Success") ){
                return ResultMap.resultError("发送短信异常");
            }

            //将生成的验证码存放到redis中
            redisService.put(phone,code);

        }catch (Exception e){
            e.printStackTrace();
            return ResultMap.resultError("业务繁忙");
        }

        return ResultMap.resultSuccess(code);

    }

    /**
     * 功能描述: 生成count位随机数
     * @Param: [count] ： 多少位随机数
     * @Return: java.lang.String 随机数
     * @Date: 2020/12/22 22:03
     */
    private String getRandomCode(int count) {

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            //生成随机数，本来是生成0-1范围的随机数，*10 变成生成1-9之间的随机数
            int code = (int)(Math.random() * 10);
            buffer.append(code);
        }
        return buffer.toString();
    }

    /**
     * 功能描述: 验证用户登录
     * @Param: [request, phone, loginPassword]
     * @Return: java.lang.Object
     * @Date: 2020/12/24 21:02
     */
    @RequestMapping("/checkLogin")
    public @ResponseBody Object checkLogin(Model model, HttpServletRequest request,String phone,String loginPassword){
        try {
            User user = userService.queryLogin(phone,loginPassword);
            request.getSession().setAttribute(Constants.SESSION_USER,user);
        }catch (Exception e){
            e.printStackTrace();
            ResultMap.resultError("登录失败！");
        }

        return ResultMap.resultSuccess();
    }

    /**
     * 功能描述: 查询下拉选的可用余额
     *       分析：在后台获取当前登录对象，之后使用获取到的对象查询可用余额，再返回前端
     * @Param: [session]
     * @Return: java.util.Map<java.lang.String,java.lang.Object>
     * @Date: 2020/12/25 11:23
     */
    @RequestMapping("getAvailableMoney")
    public @ResponseBody Map<String,Object> getAvailableMoney(HttpSession session){

        //在session中查询当前登录对象
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        Integer id = user.getId();

        //使用user开始查询可用余额
        FinanceAccount availableMoney = financeAccountService.findByUid(id);
        //传回前端
        return ResultMap.success(availableMoney.getAvailableMoney());
    }


}
