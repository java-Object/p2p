package com.bjpowernode.p2p.mapper.user;


import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 获取平台总人数
     *
     * @return
     */
    Long selectAllUserCount();

    /**
     * 功能描述: 检查该手机号是否被注册过
     *
     * @Param: [phone]
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/21 19:36
     */
    User selectUserByPhone(String phone);

    /**
     * 功能描述: 验证用户登录
     * @Param: [phone, loginPassword]
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/24 21:11
     */
    User queryLogin(String phone, String loginPassword);


}