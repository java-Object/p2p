package com.bjpowernode.p2p.web;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.support.Parameter;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.common.utils.ResultMap;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.FinanceAccountService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanController
 * PackageName:com.bjpowernode.p2p.web
 * Description:
 *
 * @date 2020/12/19 9:50
 * @author: 动力节点
 */

@SuppressWarnings("ALL")
@Controller
@RequestMapping("/loan")
public class LoanInfoController {

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false,timeout = 15000)
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan")
    public String loan(Model model,
                       @RequestParam(value = "ptype", required = false) Integer ptype,
                       @RequestParam(value = "currentPage", required = false, defaultValue = "1") Integer currentPage) {

        //查询产品列表  共20条3页　当前为第2页
        /** List<LoanInfo>
         * 返回 产品列表  共20条3页　当前为第2页
         * 每页显示的条数是固定的，
         * 请求方法提供的参数 产品类型(ptype) 当前页(current) 每页显示的条数(固定9条)
         * 请求方法返回的数据 List<LoanInfo> 、 总条数
         */
        int pageSize = 9;
        Map<String, Object> paramMap = new HashMap<String, Object>();

        if (ObjectUtils.allNotNull(ptype)) {
            paramMap.put("productType", ptype);
        }
        paramMap.put("currentPage", (currentPage - 1) * pageSize);
        paramMap.put("pageSize", pageSize);

        // VO对象 ValueObject 值对象，
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoListByPage(paramMap);
        // 要求某对象有：LoanInfo属性 还要求有一个总条数属性  PaginationVO

        // 计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        // 将数据给到页面
        model.addAttribute("loanInfoList", paginationVO.getDataList());
        model.addAttribute("totalRows", paginationVO.getTotal());
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("currentPage", currentPage);

        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("productType", ptype);
        }


        // 获取投资排行榜
        List<Map<String,Object>> investTop = loanInfoService.getInvestTop();
        model.addAttribute("investTop",investTop);

        //跳转页面
        return "loan";
    }

    @RequestMapping(value = "/loanInfo")
    public String loanInfo(Model model, Integer id , HttpSession session) {

        //获取当前登陆对象
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        if (ObjectUtils.isNotEmpty(user)) {
            //查询登陆对象的信息（余额）
            FinanceAccount financeAccount = financeAccountService.findByUid(user.getId());
            model.addAttribute("financeAccount", financeAccount);
        }
        // 获取产品信息
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo", loanInfo);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", id);
        paramMap.put("currentPage", 0);
        paramMap.put("pageSize", 10);
        // 获取投资记录
        List<BidInfo> bidInfoList = bidInfoService.querBidInfoUserByLoanId(paramMap);

        model.addAttribute("bidInfoList", bidInfoList);

        return "loanInfo";
    }

    /**
     * 功能描述:
     *
     * @Param: 跳转到注册界面
     * @Return: java.lang.String 返回的路径
     * @Date: 2020/12/21 15:28
     */
    @RequestMapping("/page")
    public String toRegister() {
        return "register";
    }


    /**
     * 功能描述: 跳转到登录界面，并且讲传递过来的url路径保存的model中
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/24 20:38
     */
    @RequestMapping("/page/login")
    public String toLogin(Model model ,String returnUrl){
        //将url路径保存到model中
        model.addAttribute("returnUrl",returnUrl);
        return "login";
    }


    /**
     * 功能描述: 退出登陆
     *    思路：
     *    1、登录的时候，是将用户对象保存到session中，
     *       那退出，也就是将用户对象从session中删除，或者销毁
     *    2、跳转界面，一般是登录界面、首页
     * @Param: [request] 使用session
     * @Return: java.lang.String 返回登录界面
     * @Date: 2020/12/24 21:32
     */
    @RequestMapping("/logout")
    public String toLogout(HttpServletRequest request){

        // 第一种方法：删除session中user的值
        // request.getSession().removeAttribute(Constants.SESSION_USER);

        // 第二种方法：销毁当前session对象
        request.getSession().invalidate();

        //跳转页面
        return "redirect:/loan/page/login";
    }

    /**
     * 功能描述: 进入我的小金库
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/24 22:15
     */
    @RequestMapping("/myCenter")
    public String toMyCenter(Model model,HttpSession session){

        //查询当前登陆对象的信息
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        model.addAttribute("user",user);

        //获取当前的余额
        FinanceAccount financeAccount = financeAccountService.findByUid(user.getId());
        model.addAttribute("financeAccount",financeAccount);

        return "myCenter";
    }

    /**
     * 功能描述: 用户投资
     *          1、修改用户的可用余额
     *          2、修改产品的剩余可投金额（控制并发，防止超卖现象的发生）
     *          3、新增一条投资记录
     * @Param: [bidMoney, loanId, session]
     * @Return: com.bjpowernode.p2p.common.Constants
     * @Date: 2020/12/27 11:23
     */
    @RequestMapping("/invest")
    public @ResponseBody ResultMap invest(@RequestParam("bidMoney") Double bidMoney,
                                          @RequestParam("loanId") Integer loanId,
                                          HttpSession session
                                          ){
        //获取已登陆的用户对象
        User user = (User) session.getAttribute(Constants.SESSION_USER);

        //用户投资
        loanInfoService.invest(loanId,bidMoney,user.getId());

        //返回提示消息
        return ResultMap.success();
    }

    /**
     * 功能描述: 跳转到充值界面
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/30 9:29
     */
    @RequestMapping("/page/toRecharge")
    public String toRecharge(){
        return "toRecharge";
    }

    /**
     * 功能描述: 跳转到充值界面的充值记录
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/30 9:30
     */
    @RequestMapping("/myRecharge")
    public String myRecharge(){
        return "myRecharge";
    }


    /**
     * 功能描述:跳转到充值界面的投资记录
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/30 9:31
     */
    @RequestMapping("/myInvest")
    public String myInvest(){
        return "myInvest";
    }

    /**
     * 功能描述: 跳转到充值界面的收益记录
     * @Param: []
     * @Return: java.lang.String
     * @Date: 2020/12/30 9:32
     */
    @RequestMapping("/myIncome")
    public String myIncome(){
        return "myIncome";
    }
}
