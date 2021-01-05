package com.bjpowernode.p2p.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Luo
 * @date 2020/12/23 9:16
 * @Package com.bjpowernode.p2p.service.user
 * class
 */

@Component
@Service(interfaceClass = RedisService.class,version = "1.0.0",timeout = 15000)
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    /**
     * 功能描述: 将验证码存到redis缓存中
     * @Param: [phone, code] 手机号、验证码
     * @Return: void
     * @Date: 2020/12/23 9:17
     */
    @Override
    public void put(String key, String value) {

        //存储到redis缓存中，并且设置保存30分钟.
        redisTemplate.opsForValue().set(key,value,30, TimeUnit.DAYS);

    }

    /**
     * 功能描述: 从数据库中查询是否有这个值！
     * @Param: [key] key
     * @Return: java.lang.Object
     * @Date: 2020/12/24 15:44
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
