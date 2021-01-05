package com.bjpowernode.p2p.service;

/**
 * @author Luo
 * @date 2020/12/23 9:13
 * @Package com.bjpowernode.p2p.service
 * class
 */
public interface RedisService {
    /**
     * 功能描述: 在redis中保存验证码，手机号作为key
     * @Param: [phone, code]
     * @Return: void
     * @Date: 2020/12/23 9:14
     */
    void put(String key, String value);

    /**
     * 功能描述: 查询数据库中是否有这个值
     * @Param: [messageCode] key
     * @Return: java.lang.Object 返回值是object，就不局限于单一方法了，可以多方法使用
     * @Date: 2020/12/24 15:42
     */
    Object get(String key);
}
