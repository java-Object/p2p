package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Luo
 * @date 2020/12/29 19:44
 * @Package com.bjpowernode.p2p.service.loan
 * class
 */
@Component
@Service(interfaceClass = IncomeRecordService.class,version = "1.0.0",timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;


    /**
     * 功能描述: 批量插入收益记录
     * @Param: [loanInfoList]
     * @Return: int
     * @Date: 2020/12/29 19:47
     */
    @Override
    public int insertIncomeRecordList(List<IncomeRecord> loanInfoList) {
        return incomeRecordMapper.insertIncomeRecordList(loanInfoList);
    }

    /**
     * 功能描述: 根据当前日期和未返还的状态，来查询将要返还的对象
     * @Param: [i]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.IncomeRecord>
     * @Date: 2020/12/29 20:48
     */
    @Override
    public List<IncomeRecord> queryIncomeRecordListByCountDateAndIncomStatus(int incomeStatus) {
        return incomeRecordMapper.selectIncomeRecordListByCountDateAndIncomStatus(incomeStatus);
    }

    /**
     * 功能描述: 更新收益状态为1
     * @Param: [updateIncomeRecord]
     * @Return: void
     * @Date: 2020/12/29 21:15
     */
    @Override
    public void updateIncomeStatusById(IncomeRecord updateIncomeRecord) {
        incomeRecordMapper.updateByPrimaryKeySelective(updateIncomeRecord);
    }
}
