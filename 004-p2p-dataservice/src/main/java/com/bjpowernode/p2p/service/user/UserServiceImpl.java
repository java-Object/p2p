package com.bjpowernode.p2p.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Service(interfaceClass = UserService.class, version = "1.0.0", timeout = 15000)
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    /**
     * 获取平台总人数
     *
     * @return
     */
    @Override
    public Long queryAllUserCount() {
        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
        if (!ObjectUtils.allNotNull(allUserCount)) {
            synchronized (this) {
                allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
                if (!ObjectUtils.allNotNull(allUserCount)) {
                    // 从数据库中获取数据
                    allUserCount = userMapper.selectAllUserCount();
                    //把数据库中的数据存到redis缓存中
                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT, allUserCount);

                }
            }
        }

        return allUserCount;
    }

    /**
     * 功能描述: 检查这个手机号是否被注册过
     *
     * @Param: [phone]
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/21 19:35
     */
    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }


    /**
     * 功能描述: 注册新用户、给新用户添加红包
     *
     * @Param: [phone, loginPassword] ： 账号、密码
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/21 20:50
     */
    @Transactional   //事务的注解，加入这注解，就可以直接使用事务操作了
    @Override
    public User queryRegister(String phone, String loginPassword) {

        //创建对象，存入数据
        User user = new User();
        user.setPhone(phone);
        user.setLoginPassword(loginPassword);
        user.setAddTime(new Date());
        user.setLastLoginTime(new Date());

        //开始创建用户
        userMapper.insertSelective(user);

        //创建成功之后在账户余额中新用户添加888的红包
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setUid(user.getId());
        financeAccount.setAvailableMoney(888.0);

        //增加余额
        financeAccountMapper.insertSelective(financeAccount);

        return user;
    }

    /**
     * 功能描述: 验证用户登录
     * @Param: [phone, loginPassword]
     * @Return: com.bjpowernode.p2p.model.user.User
     * @Date: 2020/12/24 21:09
     */
    @Override
    public User queryLogin(String phone, String loginPassword) {
        return userMapper.queryLogin(phone,loginPassword);
    }



}
