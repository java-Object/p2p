package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Luo
 * @date 2020/12/30 19:58
 * @Package com.bjpowernode.p2p.service.loan
 * class
 */
@Component
@Service(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 功能描述: 使用UUID + redis自增生成全局唯一的订单号码
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/30 19:58
     */
    @Override
    public String getRechargeNo() {

        ////UUID：初始是36位,使用replaceAll方法去掉“-”号，还剩下32位
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        //使用redis数据类型的自增获取不可重复的值
        Long num = redisTemplate.boundValueOps("rechargeNo").increment();

        return uuid + num.toString();
    }

    /**
     * 功能描述: 使用时间戳 + redis自增生产全网唯一订单号码
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/31 9:05
     */
    @Override
    public String getWeixinRechargeNo() {

        Long l = System.currentTimeMillis();
        Long no = redisTemplate.boundValueOps("rechargeNo").increment();

        return l.toString() + no.toString();
    }


    /**
     * 功能描述: 增加一条充值记录
     * @Param: [rechargeRecord]
     * @Return: int
     * @Date: 2020/12/30 20:14
     */
    @Override
    public int insertRecharRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }
}
