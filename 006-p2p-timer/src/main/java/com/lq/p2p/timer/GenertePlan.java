package com.lq.p2p.timer;
import java.util.Date;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.FinanceAccountService;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Luo
 * @date 2020/12/29 11:11
 * @Package com.lq.p2p.timer
 * class 定时任务方法，无返回值
 */
@Component
public class GenertePlan {

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false,timeout = 15000)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false,timeout = 15000)
    private UserService userService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false,timeout = 15000)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = IncomeRecordService.class,version = "1.0.0",check = false,timeout = 15000)
    private IncomeRecordService incomeRecordService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false,timeout = 15000)
    private FinanceAccountService financeAccountService;

    /**
     * 功能描述:  凌晨2点钟，触发redis中的数据
     * 更新redis中的数据，首页面（历史平均收益率、平台总人数、累计投资总金额）
     * @Param: []
     * @Return: void
     * @Date: 2020/12/29 11:13
     */
//    @Scheduled(cron = "1,2,3,5,6,8 * * * * *")
    public void updataindexData(){

        System.out.println("--------------更新首页面redis中的数据开始----------------");
        //清除redis中的数据，然后重新保存一下
        //历史平均年收益率
        redisTemplate.delete(Constants.HISTORY_AVG_RATE);
        //获取平台总人数
        redisTemplate.delete(Constants.ALL_USER_COUNT);
        //获取累计成交金额
        redisTemplate.delete(Constants.ALL_BID_MONEY);

        //开始更新
        //历史平均年收益率
        Double historyAvgRate = loanInfoService.queryHistoryAvgRate();
        System.out.println("历史平均年收益率historyAvgRate------------------>" + historyAvgRate);
        //获取平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        System.out.println("获取平台注册总人数allUserCount-----------------"+allUserCount);
        //获取累计成交金额
        Double aDouble = bidInfoService.queryAllBidMoney();
        System.out.println("获取累计成交金额aDouble----------------------" + aDouble);

        System.out.println("--------------更新首页面redis中的数据结束----------------");
    }

    /**
     * 功能描述: 将剩余可投金额为0和未满标的产品，设置为已满标
     *           productStatus : 0 未满标
     *           productStatus : 1 已满标
     *           productStatus : 2 满标且生成收益计划
     * @Param: []
     * @Return: void
     * @Date: 2020/12/29 13:03
     */
//    @Scheduled(cron = "5,10,15,20,25 * * * * *")
    public void updataLoanInfoProductStatus(){

        System.out.println("将剩余可投金额为0和未满标的产品，设置为已满标———开始");

        /*
            查询条件有两个，这两个条件必须都满足:
                1、剩余可投金额为0.
                2、产品状态为0
                参数一：代表可投金额
                参数二：代表产品未满标状态
        */


        //使用更新的方式：通过in函数进行批量更新
        //批量查询要更新的产品id
        List<Integer> loanInfoIds = loanInfoService.queryLoanInfoIdsByLeftProductMoneyAndProductStatus(0,0);
        System.out.println("更新的产品id=loanInfoIds-----------》"+loanInfoIds);

        //根据产品id设置数据为已满标状态
        int updataCount = loanInfoService.updateLoanInfoByIds(loanInfoIds);
        System.out.println("更新数据为已满标状态的有updataCount：--------》"+updataCount);

        System.out.println("将剩余可投金额为0和未满标的产品，设置为已满标———结束");

    }


    /**
     * 功能描述: 生成收益计划
     *          1、查询已满标产品，List<LoanInfo>
     *          2、便利集合
     *          3、创建 IncomeRecord 对象，赋值，loanId、bidId、uId
     *          4、计算收益返还时间
     *          5、计算收益金额
     *          6、将 IncomeRecord 对象插入到数据库中
     *          7、更新 loanInfo 的 productStatus 为2，代表满标且生成收益计划
     * @Param: []
     * @Return: void
     * @Date: 2020/12/29 17:35
     */
//    @Scheduled(cron = "0 * * * * *")
    public void generateIncomePlan(){

        //1、查询已满标产品，List<LoanInfo>：根据产品状态为1的，就代表已满标，查询已满标的列表
        List<LoanInfo> loanInfoList = loanInfoService.queryLoanInfoListByProductStatus(1);

        //2、遍历已满标的产品集合
        for (LoanInfo loanInfo : loanInfoList) {

            //3、封装收益列表
            List<IncomeRecord> incomeRecordList = new ArrayList<>();

            //根据当前已经满标的产品id，查询出它对应的投资列表
            List<BidInfo> bidInfoList =  bidInfoService.queryBidInfosByLoanId(loanInfo.getId());
            //查到就执行下面
            if (ObjectUtils.isNotEmpty(bidInfoList)){
                for (BidInfo bidInfo : bidInfoList) {
                    //收益记录应该是多条
                    //所以一条记录对应一条收益记录
                    IncomeRecord incomeRecord = new IncomeRecord();
                    incomeRecord.setLoanId(loanInfo.getId());
                    incomeRecord.setBidId(bidInfo.getId());
                    incomeRecord.setUid(bidInfo.getUid());
                    incomeRecord.setBidMoney(bidInfo.getBidMoney());
                    //收益返还的状态，0代表未返还，1代表已返还
                    incomeRecord.setIncomeStatus(0);
                    //计算收益金额已经收益日期
                    //计算收益日期时，需要注意：新手宝不同于其他的产品，它是以天为单位，其他是以月为单位
                    Date incomeDate = null;
                    Double incomeMoney = null;
                    if (loanInfo.getProductType() > 0){
                        //优选、散标的产品
                        //4、计算收益返还日期
                        //返还日期为下单时间的后一天的后一个月，T+1
                        Date bidTime = DateUtils.addDays(bidInfo.getBidTime(), 1);
                        //获取周期
                        incomeDate = DateUtils.addMonths(bidTime, loanInfo.getCycle());
                        //设置到收益记录中
                        incomeRecord.setIncomeDate(incomeDate);

                        //5、设置收益的金额
                        Double bidMoney = bidInfo.getBidMoney();
                        Double bm = bidMoney * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * 30;
                        incomeMoney = ((Long)Math.round(bm * 100)).doubleValue() / 100;
                        //将金额存入到对象中
                        incomeRecord.setIncomeMoney(incomeMoney);
                    } else{
                        //新手宝
                        //计算收益返还日期
                        Date bidTime = DateUtils.addDays(bidInfo.getBidTime(), 1);
                        //获取周期
                        incomeDate = DateUtils.addDays(bidTime, loanInfo.getCycle());
                        //设置到收益记录中
                        incomeRecord.setIncomeDate(incomeDate);

                        //5、设置收益的金额
                        Double bidMoney = bidInfo.getBidMoney();
                        Double bm = bidMoney * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                        incomeMoney = ((Long)Math.round(bm * 100)).doubleValue() / 100;
                        //将金额存入到对象中
                        incomeRecord.setIncomeMoney(incomeMoney);

                    }
                    //存入list
                    incomeRecordList.add(incomeRecord);
                }
                //6、将 IncomeRecord 对象插入到数据库中
                int insertCount = incomeRecordService.insertIncomeRecordList(incomeRecordList);
                System.out.println("insertCount-------------->"+insertCount);

                // 7、更新 loanInfo 的 productStatus 为2，代表满标且生成收益计划
                //需要将产品的已满标更新为满标且生成收益计划
                LoanInfo updateLoanInfo = new LoanInfo();
                updateLoanInfo.setId(loanInfo.getId());
                updateLoanInfo.setProductStatus(2);
                int updateCount = loanInfoService.updateProductStatusByLoanId(updateLoanInfo);

                //如果更新失败，就报异常
                if (updateCount <= 0){
                    throw new RuntimeException("更新产品状态失败！");
                }

            }
        }
    }


    /**
     * 功能描述: 生成收益返还
     *              根据返还的收益日期，将利息和本金返还到账户中
     *              并更新收益记录的状态为1，代表已返还
     *
     * @Param: []
     * @Return: void
     * @Date: 2020/12/29 20:42
     */
    @Scheduled(cron = "0 * * * * *")
    public void generateIncomeBack(){
        //根据当前的日期，查询为返还的收益记录
        //根据当前日期和未返还的状态，来查询将要返还的利息
        List<IncomeRecord> incomeRecordList = incomeRecordService.queryIncomeRecordListByCountDateAndIncomStatus(0);

        //查询到要返还的数据才执行下面
        if (ObjectUtils.isNotEmpty(incomeRecordList)){
            //遍历集合
            for (IncomeRecord incomeRecord : incomeRecordList) {
                //返还收益
                //将 bidMoney 返还到账户中
                Double bidMoney = incomeRecord.getBidMoney();
                //将 incomeMoney 返还到账户中
                Double incomeMoney = incomeRecord.getIncomeMoney();

                //查询账户信息
                Integer uid = incomeRecord.getUid();
                //根据当前uid查询账户对象
                FinanceAccount financeAccount = financeAccountService.findByUid(uid);

                //更新账户信息
                Double availableMoney = financeAccount.getAvailableMoney();
                financeAccount.setAvailableMoney(availableMoney + bidMoney + incomeMoney);
                int updateCount = financeAccountService.updateAvailableMoneyById(financeAccount);

                //更新收益状态为1，代表当前的本息已返还到账户中
                if (updateCount > 0){
                    //返还收益成功
                    //更新收益状态为1，代表已返还
                    IncomeRecord updateIncomeRecord = new IncomeRecord();
                    updateIncomeRecord.setId(incomeRecord.getId());
                    updateIncomeRecord.setIncomeStatus(1);
                    incomeRecordService.updateIncomeStatusById(updateIncomeRecord);
                    System.out.println("返还收益成功--------------------------");
                }

            }
        }






    }



}
