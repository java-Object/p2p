package com.bjpowernode.p2p.service.loan;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.user.User;

import java.util.*;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.RedisService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("AlibabaTransactionMustHaveRollback")
@Component
@Service(interfaceClass = LoanInfoService.class, version = "1.0.0", timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Double queryHistoryAvgRate() {
        //redisTemplate.setKeySerializer(new StringRedisSerializer());
        // 第一次优化的思路：
        /**
         *  如果网址的访问量特别高，那么会导致查询数据库的压力变大.
         *  年化收益率，可以存放到redis缓存中.
         *  逻辑：
         *      当用户访问首页时，我们先从缓存中查找历史年华收益率
         *    如果缓存中有： 直接从缓存中
         *    如果缓存中没有： 先从数据库中查询，然后在存放到缓存中
         *
         *    在多线程高并发请求redis缓存的时候可能会发生:缓存穿透
         *
         *    synchronized 可以修饰方式
         *    synchronized() {
         *
         *    }
         *
         *    解决缓存穿透问题的方案:  双重验证+同步锁机制
         */

        Double historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);

        if (!ObjectUtils.allNotNull(historyAvgRate)) {
            synchronized (this) {
                historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);
                if (!ObjectUtils.allNotNull(historyAvgRate)) {
                    System.out.println("从数据库中获取数据");
                    // 从数据库中获取平均年化收益率
                    historyAvgRate = loanInfoMapper.selectHistoryAvgRate();
                    // 将数据库中的数据保存到redis缓存中
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVG_RATE, historyAvgRate);
                } else {
                    System.out.println("从redis中获取数据");
                }
            }
        } else {
            System.out.println("从redis中获取数据");
        }

        /*Double historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);

        if (!ObjectUtils.allNotNull(historyAvgRate)) {
            System.out.println("从数据库中获取数据");
            // redis缓存中没有数据
            historyAvgRate = loanInfoMapper.selectHistoryAvgRate();
            // 将历史平均年化收益率保存到redis缓存中
            redisTemplate.opsForValue().set(Constants.HISTORY_AVG_RATE,historyAvgRate,7, TimeUnit.DAYS);
        } else {
            System.out.println("从redis缓存中获取数据");
        }*/
        return historyAvgRate;
    }


    /**
     * 根据产品类型获取产品信息列表
     *
     * @param paramMap 起始页 每页显示的条数 产品类型
     * @return 产品列表
     */
    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {

        return loanInfoMapper.selectLoanInfoListByProduct(paramMap);
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();
        // 查询总条数
        Long total = loanInfoMapper.selectLoanInfoAllCount(paramMap);
        paginationVO.setTotal(total);

        // 查询产品列表
        List<LoanInfo> loanInfos = loanInfoMapper.selectLoanInfoListByProduct(paramMap);
        paginationVO.setDataList(loanInfos);

        return paginationVO;
    }

    /**
     * 功能描述: 用户投资
     * @Param: [loanId, bidMoney, id]
     * @Return: void
     * @Date: 2020/12/27 11:36
     */
    @Override
    @Transactional
    public void invest(Integer loanId, Double bidMoney, Integer id) {
        //修改用户可用余额
        //根据id查询对象
        FinanceAccount financeAccount = financeAccountMapper.selectByUid(id);
        //判断 financeAccount 是否为空，查询到对象就可以执行修改余额语句
        if (ObjectUtils.isNotEmpty(financeAccount)){
            //更新用户的可用余额
            FinanceAccount updateFinanceAccount = new FinanceAccount();
            updateFinanceAccount.setId(financeAccount.getId());
            //账户余额 减去 页面输入的投资金额
            Double availableMoney = financeAccount.getAvailableMoney() - bidMoney;
            updateFinanceAccount.setAvailableMoney(availableMoney);
            //将账户信息更新到数据库中
            //这种方式更新：即使uid没有赋值，会将原本的uid给覆盖成空
//            financeAccountMapper.updateByPrimaryKey(updateFinanceAccount);
            //使用这种方式更新：即使uid没有赋值，也不会对原本的uid进行覆盖操作
            financeAccountMapper.updateByPrimaryKeySelective(updateFinanceAccount);

            /*
              更新剩余可投金额，并且防止超卖现象的发生
              线程并发的场景下：
                    如果剩余可投金额，还剩下1000元，有1000个人去争夺，会发生超卖事件，可投金额会变成负数，不能让它超卖，成为负数。
                  解决方法：
                    数据库的乐观锁：
                        通过数据库表的字段，来进行控制
                        比如1000个人去争抢资源，有抢到的，有没抢到的。
                        update b_loan_info set left_product_money= left_product_money - #{bidMoney},version = version+1 where id = #{id} and version = #{version}

                    数据库的悲观锁:
                    选择表的引擎：Innodb具备事务的特性
                                 其他的存储引擎，不具备事务的特性
                    在操作数据库时：
                          表锁：进行数据的增删改时，对整张表加了锁，当事务完成后，才允许另外的事务进行修改的操作
                          行锁：进行数据的增删改时，对操作的那一行进行加锁。当事务完成后，才允许另外的事务进行修改的操作
            */
            //根据loanId，查询产品数据
            LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);

            //更新剩余可投金额，并且防止超卖现象发生
            Integer loanInfoUpdateCount = loanInfoMapper.updateLeftProductMoenyByLoanId(loanId,loanInfo.getVersion(),bidMoney);

            if (loanInfoUpdateCount > 0){
                //新增一条投资数据
                BidInfo bidInfo = new BidInfo();
                bidInfo.setLoanId(loanId);
                bidInfo.setUid(id);
                bidInfo.setBidMoney(bidMoney);
                bidInfo.setBidTime(new Date());
                bidInfo.setBidStatus(1);
                bidInfoMapper.insertSelective(bidInfo);

                //投资排行榜功能实现
                //通过传过来的id查询对象信息
                User user = userMapper.selectByPrimaryKey(id);


                // zSet集合：key和value，key是集合名称，value是手机号码，soure是投资金额
                //参数1是集合名称
                //add方法参数1：value
                //add方法参数2：分数，soure

                //当前方法并没有进行叠加，而是进行覆盖，不适合用来做排行榜
//                redisTemplate.boundZSetOps("investTop").add(user.getPhone(),bidMoney);

                //使用 incrementScore 可以对分数进行累加操作
                redisTemplate.boundZSetOps("investTop").incrementScore(user.getPhone(),bidMoney);

            }


        }
    }

    /**
     * 功能描述: 获取投资排行榜
     * @Param: []
     * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Date: 2020/12/27 19:40
     */
    @Override
    public List<Map<String, Object>> getInvestTop() {
        List<Map<String , Object>> resultList = new ArrayList<>();

        //按照分数倒叙获取
        // reverseRangeByScoreWithScores 该方法是查询分数区间
//        redisTemplate.boundZSetOps("investTop").reverseRangeByScoreWithScores(0,100000000);

        // reverseRangeWithScores 进行分页查询
        Set<ZSetOperations.TypedTuple<Object>> investTop = redisTemplate.boundZSetOps("investTop").reverseRangeWithScores(0, 5);

        for (ZSetOperations.TypedTuple tuple : investTop){
            Map<String ,Object> map = new HashMap<>();
            String phone = (String) tuple.getValue();
            Double tupleBidMoney = tuple.getScore();
            map.put("phone",phone);
            map.put("bidMoney",tupleBidMoney);

            resultList.add(map);
        }

        System.out.println("resultList---------------------->" + resultList);

        return resultList;
    }

    /**
     * 功能描述: 批量查询要更新的产品id
     * @Param: [i, i1]
     * @Return: java.util.List<java.lang.Integer>
     * @Date: 2020/12/29 13:12
     */
    @Override
    public List<Integer> queryLoanInfoIdsByLeftProductMoneyAndProductStatus(int leftProductMoney, int productStatus) {
        return loanInfoMapper.selectLoanInfoIdsByLeftProductMoneyAndProductStatus(leftProductMoney,productStatus);
    }

    /**
     * 功能描述:
     * @Param: 批量根据产品id设置数据为已满标状态
     * @Return: java.lang.Integer
     * @Date: 2020/12/29 13:25
     */
    @Override
    public int updateLoanInfoByIds(List<Integer> loanInfoIds) {
        return loanInfoMapper.updateLoanInfoByIds(loanInfoIds);
    }


    /**
     * 功能描述: 根据产品状态为1的查询已满标的列表
     * @Param: [i]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.LoanInfo>
     * @Date: 2020/12/29 17:46
     */
    @Override
    public List<LoanInfo> queryLoanInfoListByProductStatus(int productStatus) {
        return loanInfoMapper.selectLoanInfoListByProductStatus(productStatus);
    }

    /**
     * 功能描述: 将产品的已满标更新为满标且生成收益计划
     * @Param: [updateLoanInfo]
     * @Return: int
     * @Date: 2020/12/29 20:00
     */
    @Override
    public int updateProductStatusByLoanId(LoanInfo updateLoanInfo) {
        //只更新传递过来字段的值，其余保持原样
        return loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
    }
}
