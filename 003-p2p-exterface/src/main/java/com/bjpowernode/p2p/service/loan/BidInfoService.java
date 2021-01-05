package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoService {

    /**
     * 获取平台累计投资金额
     *
     * @return
     */
    Double queryAllBidMoney();


    /**
     * 根据产品ID 查询投资记录
     *
     * @param paramMap
     * @return
     */
    List<BidInfo> querBidInfoUserByLoanId(Map<String, Object> paramMap);


    /**
     * 功能描述: 根据当前已经满标的产品id，查询出它对应的投资列表
     * @Param: [id]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.BidInfo>
     * @Date: 2020/12/29 17:56
     */
    List<BidInfo> queryBidInfosByLoanId(Integer loanId);
}
