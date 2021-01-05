package com.bjpowernode.p2p.mapper.loan;


import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    /**
     * 获取平台累计投资金额
     *
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据产品ID查询最近十条投资记录
     *
     * @param paramMap
     * @return
     */
    List<BidInfo> selectBidInfoUserByLoanId(Map<String, Object> paramMap);

    /**
     * 功能描述:根据当前已经满标的产品id，查询出它对应的投资列表
     * @Param: [loanId]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.BidInfo>
     * @Date: 2020/12/29 17:57
     */
    List<BidInfo> selectBidInfosByLoanId(Integer loanId);
}