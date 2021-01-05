package com.bjpowernode.p2p.mapper.user;


import com.bjpowernode.p2p.model.user.FinanceAccount;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**
     * 功能描述: 根据用户id查询用户余额
     * @Param: [id]
     * @Return: com.bjpowernode.p2p.model.user.FinanceAccount
     * @Date: 2020/12/25 15:35
     */
    FinanceAccount selectByUid(Integer id);
}