package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

/**
 * @author Luo
 * @date 2020/12/30 19:56
 * @Package com.bjpowernode.p2p.service.loan
 * class
 */
public interface RechargeRecordService {

    /**
     * 功能描述: 使用UUID + redis自增生成全局唯一的订单号码
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/30 19:57
     */
    String getRechargeNo();

    /**
     * 功能描述: 使用时间戳 + redis自增
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/31 9:05
     */
    String getWeixinRechargeNo();


    /**
     * 功能描述: 增加一条充值记录
     * @Param: [rechargeRecord]
     * @Return: int
     * @Date: 2020/12/30 20:13
     */
    int insertRecharRecord(RechargeRecord rechargeRecord);
}
