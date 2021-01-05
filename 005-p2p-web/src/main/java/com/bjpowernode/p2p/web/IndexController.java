package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;

    @RequestMapping("/index")
    public String toIndex(Model model) {

        //模拟多线程高并发访问
       /* ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 10000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Double historyAvgRate = loanInfoService.queryHistoryAvgRate();
                    model.addAttribute(Constants.HISTORY_AVG_RATE,historyAvgRate);
                }
            });
        }
        executorService.shutdownNow();*/

        //动力金融网历史平均年化收益率
        Double historyAvgRate = loanInfoService.queryHistoryAvgRate();
        model.addAttribute(Constants.HISTORY_AVG_RATE, historyAvgRate);

        // 获取平台注册人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT, allUserCount);

        // 获取累计成交金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY, allBidMoney);


        // 页面显示的三种产品只是product_type的值不一样

        // 获取新手宝产品信息 product_type=0,起始位置 currentPage=0, pageSize=1
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 1);
        paramMap.put("productType", Constants.PRODUCT_TYPE_X);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList", xLoanInfoList);


        // 获取优选类产品信息 product_type=1,起始位置 currentPage=0, pageSize=4
        paramMap.put("pageSize", 4);
        paramMap.put("productType", Constants.PRODUCT_TYPE_U);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList", uLoanInfoList);

        // 获取散标类产品信息 product_type=2,起始位置 currentPage=0, pageSize=8
        paramMap.put("pageSize", 8);
        paramMap.put("productType", Constants.PRODUCT_TYPE_S);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList", sLoanInfoList);

        return "index";
    }
}
