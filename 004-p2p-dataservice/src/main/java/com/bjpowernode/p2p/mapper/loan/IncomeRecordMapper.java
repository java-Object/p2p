package com.bjpowernode.p2p.mapper.loan;


import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    /**
     * 功能描述: 批量插入收益记录
     * @Param: [loanInfoList]
     * @Return: int
     * @Date: 2020/12/29 19:47
     */
    int insertIncomeRecordList(List<IncomeRecord> loanInfoList);

    /**
     * 功能描述: 根据当前日期和未返还的状态，来查询将要返还的对象集
     * @Param: [incomeStatus]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.IncomeRecord>
     * @Date: 2020/12/29 20:50
     */
    List<IncomeRecord> selectIncomeRecordListByCountDateAndIncomStatus(int incomeStatus);
}