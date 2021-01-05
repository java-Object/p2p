package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * @author Luo
 * @date 2020/12/25 12:24
 * @Package com.bjpowernode.p2p.service.loan
 * class
 */
public interface FinanceAccountService {

    /**
     * 功能描述: 根据用户id查询账户信息
     * @Param: [id]
     * @Return: com.bjpowernode.p2p.model.user.FinanceAccount
     * @Date: 2020/12/25 12:26
     */
    FinanceAccount findByUid(Integer id);

    /**
     * 功能描述: 更新账户信息
     * @Param: [financeAccount]
     * @Return: int
     * @Date: 2020/12/29 21:09
     */
    int updateAvailableMoneyById(FinanceAccount financeAccount);
}
