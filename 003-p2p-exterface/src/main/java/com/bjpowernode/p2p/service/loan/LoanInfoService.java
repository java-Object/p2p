package com.bjpowernode.p2p.service.loan;


import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

public interface LoanInfoService {

    /**
     * 查询历史平均年华收益率
     *
     * @return
     */
    Double queryHistoryAvgRate();

    /**
     * 根据产品类型获取产品信息
     *
     * @param paramMap
     */
    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 根据产品ID查询产品详情
     *
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoById(Integer id);

    /**
     * 分页查询产品列表
     *
     * @param paramMap
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap);

    /**
     * 功能描述: 用户投资
     * @Param: [loanId, bidMoney, id]
     * @Return: void
     * @Date: 2020/12/27 11:35
     */
    void invest(Integer loanId, Double bidMoney, Integer id);

    /**
     * 功能描述: 获取投资排行榜
     * @Param: []
     * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Date: 2020/12/27 19:39
     */
    List<Map<String, Object>> getInvestTop();

    /**
     * 功能描述:
     * @Param: 批量查询要更新的产品id
     * @Return: java.util.List<java.lang.Integer>
     * @Date: 2020/12/29 13:12
     */
    List<Integer> queryLoanInfoIdsByLeftProductMoneyAndProductStatus(int leftProductMoney, int productStatus);

    /**
     * 功能描述: 批量根据产品id设置数据为已满标状态
     * @Param: [loanInfoIds]
     * @Return: java.lang.Integer
     * @Date: 2020/12/29 13:23
     */
    int updateLoanInfoByIds(List<Integer> loanInfoIds);

    /**
     * 功能描述: 根据产品状态为1的查询已满标的列表
     * @Param: [i]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.LoanInfo>
     * @Date: 2020/12/29 17:45
     */
    List<LoanInfo> queryLoanInfoListByProductStatus(int productStatus);

    /**
     * 功能描述: 将产品的已满标更新为满标且生成收益计划
     * @Param: [updateLoanInfo]
     * @Return: int
     * @Date: 2020/12/29 19:59
     */
    int updateProductStatusByLoanId(LoanInfo updateLoanInfo);
}
