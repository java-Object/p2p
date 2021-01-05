package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;

/**
 * @author Luo
 * @date 2020/12/29 19:40
 * @Package com.bjpowernode.p2p.service.loan
 * class
 */
public interface IncomeRecordService {
    /**
     * 功能描述: 插入收益记录
     * @Param: [incomeRecordList]
     * @Return: int
     * @Date: 2020/12/29 19:42
     */
    int insertIncomeRecordList(List<IncomeRecord> loanInfoList);

    /**
     * 功能描述: 根据当前日期和未返还的状态，来查询将要返还的对象
     * @Param: [i]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.IncomeRecord>
     * @Date: 2020/12/29 20:47
     */
    List<IncomeRecord> queryIncomeRecordListByCountDateAndIncomStatus(int incomeStatus);

    /**
     * 功能描述: 更新收益状态为1
     * @Param: [updateIncomeRecord]
     * @Return: void
     * @Date: 2020/12/29 21:14
     */
    void updateIncomeStatusById(IncomeRecord updateIncomeRecord);
}
