package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;

public interface UserService {

    /**
     * 获取平台总人数
     *
     * @return
     */
    Long queryAllUserCount();

    /**
     * 功能描述:检查手机号有没有被注册过
     *
     * @Param: [phone]
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/21 19:34
     */
    User queryUserByPhone(String phone);

    /**
     * 功能描述: 注册新用户
     *
     * @Param: [phone, loginPassword] :账号、密码
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/21 20:47
     */
    User queryRegister(String phone, String loginPassword);

    /**
     * 功能描述: 验证用户登录
     * @Param: [phone, loginPassword]
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/24 21:09
     */
    User queryLogin(String phone, String loginPassword);


}
