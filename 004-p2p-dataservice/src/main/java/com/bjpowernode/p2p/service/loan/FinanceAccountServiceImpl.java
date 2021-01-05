package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.service.loan.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Luo
 * @date 2020/12/25 12:27
 * @Package com.bjpowernode.p2p.service.loan
 * class
 */

@Service(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 15000)
@Component
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    /**
     * 功能描述: 根据用户id查询用户信息
     * @Param: [id]
     * @Return: com.bjpowernode.p2p.model.user.FinanceAccount
     * @Date: 2020/12/25 13:20
     */
    @Override
    public FinanceAccount findByUid(Integer id) {
        return financeAccountMapper.selectByUid(id);
    }

    /**
     * 功能描述: 更新账户信息
     * @Param: [financeAccount]
     * @Return: int
     * @Date: 2020/12/29 21:09
     */
    @Override
    public int updateAvailableMoneyById(FinanceAccount financeAccount) {
        return financeAccountMapper.updateByPrimaryKeySelective(financeAccount);
    }
}


