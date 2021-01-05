package com.bjpowernode.p2p.mapper.loan;


import com.bjpowernode.p2p.model.loan.LoanInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    /**
     * 查询平台历史年华收益率
     *
     * @return
     */
    Double selectHistoryAvgRate();

    /**
     * 根据产品类型获取产品信息列表
     *
     * @param paramMap
     * @return
     */
    List<LoanInfo> selectLoanInfoListByProduct(Map<String, Object> paramMap);

    /**
     * 根据产品ID查询产品的总条数
     *
     * @param paramMap
     * @return
     */
    Long selectLoanInfoAllCount(Map<String, Object> paramMap);

    /**
     * 功能描述: 更新剩余可投金额，并且防止超卖现象发生
     * @Param: [loanId, version, bidMoney]
     * @Return: java.lang.Integer
     * @Date: 2020/12/27 15:30
     */
    Integer updateLeftProductMoenyByLoanId(@Param("loanId") Integer loanId,
                                           @Param("version") Integer version,
                                           @Param("bidMoney") Double bidMoney);

    /**
     * 功能描述: 批量查询要更新的产品id
     * @Param: [leftProductMoney, productStatus]
     * @Return: java.util.List<java.lang.Integer>
     * @Date: 2020/12/29 13:13
     */
    List<Integer> selectLoanInfoIdsByLeftProductMoneyAndProductStatus(@Param("leftProductMoney") int leftProductMoney, @Param("productStatus") int productStatus);

    /**
     * 功能描述: 批量根据产品id设置数据为已满标状态
     * @Param: [loanInfoIds]
     * @Return: java.lang.Integer
     * @Date: 2020/12/29 13:25
     */
    int updateLoanInfoByIds( List<Integer> loanInfoIds);

    /**
     * 功能描述: 根据产品状态为1的查询已满标的列表
     * @Param: [productStatus]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.LoanInfo>
     * @Date: 2020/12/29 17:47
     */
    List<LoanInfo> selectLoanInfoListByProductStatus(int productStatus);
}