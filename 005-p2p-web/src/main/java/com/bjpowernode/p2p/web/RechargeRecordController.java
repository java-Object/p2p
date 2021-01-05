package com.bjpowernode.p2p.web;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.common.utils.HttpClientUtils;
import com.bjpowernode.p2p.common.utils.ResultMap;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.github.wxpay.sdk.WXPayUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Luo
 * @date 2020/12/30 9:14
 * @Package com.bjpowernode.p2p.web
 * class
 */
@Controller
@RequestMapping("/loan")
public class RechargeRecordController {

    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",check = false,timeout = 15000)
    private RechargeRecordService rechargeRecordService;

    /**
     * 功能描述: 微信支付
     * @Param: [bidMoney, session]
     * @Return: java.lang.String
     * @Date: 2020/12/30 17:57
     */
    @RequestMapping("/recharge/wexinpay")
    public String toWexinpay(Double bidMoney, HttpSession session , Model model){

        //获取当前登录对象
        User user = (User) session.getAttribute("user");

        if (ObjectUtils.isNotEmpty(user)) {

            //生成一条充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setUid(user.getId());
            rechargeRecord.setRechargeMoney(bidMoney);
            rechargeRecord.setRechargeDesc("微信充值");
            rechargeRecord.setRechargeTime(new Date());

            //充值状态：
            //0 充值中
            //1 充值成功
            //2 充值失败
            rechargeRecord.setRechargeStatus("0");


            //充值订单编号，全局唯一，需要根据这个编号去支付宝查询，支付状态
            /**
             * 生成全局唯一的订单号码：
             *      UUID + 时间戳
             *      时间戳 + Redis数值类型的自增
             *
             * 支付宝提供的订单号码最多为64位
             * 微信支付提供的订单号码最多为32位
             *      微信支付的订单号码不能超过32位
             */
            String rechargeNo = rechargeRecordService.getWeixinRechargeNo();
            rechargeRecord.setRechargeNo(rechargeNo);

            int insertCount = rechargeRecordService.insertRecharRecord(rechargeRecord);


            //将订单编号、订单类型、订单金额传递到页面中
            model.addAttribute("total_fee", bidMoney);
            model.addAttribute("out_trade_no", rechargeNo);
            model.addAttribute("body", "微信充值");


            //跳转到微信支付的页面
            return "toWxpay";

        }

        model.addAttribute("trade_msg","充值失败");
        return "toRechargeBack";
    }


    /**
     * 功能描述: 后台生成二维码图片，通过 response 来响应
     *              1、调用支付工程
     *                   支付工程调用微信支付接口，返回 code_url
     *              2、获取到 code_url，根据这个地址，生成二维码图片信息
     *              3、响应到 img 标签中，有浏览器解析，显示二维码图片
     * @Param: []
     * @Return: void
     * @Date: 2020/12/30 21:30
     */
//    @RequestMapping("/recharge/getWxpayQrCode")
//    public void getWxpayQrCode(@RequestParam("out_trade_no") String out_trade_no,
//                               @RequestParam("total_fee") Double total_fee,
//                               @RequestParam("body") String body,
//                               HttpServletResponse response) throws Exception {
//        //2、获取到 code_url，根据这个地址，生成二维码图片信息
//        String url = "http://localhost:9090/pay/generateWxpayCodeUrl";
//
//        //封装集合参数
//        Map<String,Object> params = new HashMap<>();
//
//        params.put("out_trade_no",out_trade_no);
//        params.put("total_fee",total_fee);
//        params.put("body",body);
//
//        String resultXml = HttpClientUtils.doPost(url, params);
//
//        //解析xml数 据
//        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
//
//        System.out.println("resultMap : : "+resultMap);
//
//        String return_code = resultMap.get("return_code");
//        String result_code = resultMap.get("result_code");
//
//        //请求成功
//        if(StringUtils.equals(return_code,"SUCCESS")){
//            //获取业务结果
//            if(StringUtils.equals(result_code,"SUCCESS")){
//                //获取code_url
//                //微信支付的地址
//                String code_url = resultMap.get("code_url");
//                //生成二维码
//                //生成矩阵对象
//                //参数1：生成二维码的内容
//                //参数2：QR_CODE代表二维码
//                //参数3：二维码的宽度
//                //参数4：二维码的高度
//                //参数5：编码字符集
//                Map<EncodeHintType,String> hints = new HashMap<>();
//                hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
//                BitMatrix bitMatrix = new MultiFormatWriter().encode(
//                        code_url,
//                        BarcodeFormat.QR_CODE,
//                        300,
//                        300,
//                        hints
//                );
//
//                //通过流对象，把它响应回去
//                ServletOutputStream outputStream = response.getOutputStream();
//
//                //以图片的方式用流将它写入到浏览器中
//                //由浏览器进行解析
//                //参数1就是二维码的矩阵对象
//                //参数2就是图片生成的格式
//                //参数3响应流
//                MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);
//
//                outputStream.flush();
//
//                outputStream.close();
//            }
//
//        }
//
//
//    }



    /**
     * 方式2：使用qrious前端js插件，生成二维码图片地址
     *      响应json数据
     *          {
     *              success:true/false,
     *              msg:xxx,
     *              data:code_url
     *          }
     * @param out_trade_no
     * @param total_fee
     * @param body
     * @param response
     * @throws Exception
     */
    @RequestMapping("/recharge/getWxpayQrCode")
    @ResponseBody
    public ResultMap<String> getWxpayQrCode(@RequestParam("out_trade_no") String out_trade_no,
                                            @RequestParam("total_fee") Double total_fee,
                                            @RequestParam("body") String body,
                                            HttpServletResponse response) throws Exception {
        String url = "http://localhost:9090/pay/generateWxpayCodeUrl";

        //封装集合参数
        Map<String,Object> params = new HashMap<>();

        params.put("out_trade_no",out_trade_no);
        params.put("total_fee",total_fee);
        params.put("body",body);

        String resultXml = HttpClientUtils.doPost(url, params);

        //解析xml数据
        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

        System.out.println("resultMap : : "+resultMap);

        String return_code = resultMap.get("return_code");
        String result_code = resultMap.get("result_code");

        //请求成功
        if(StringUtils.equals(return_code,"SUCCESS")){
            //获取业务结果
            if(StringUtils.equals(result_code,"SUCCESS")){
                //获取code_url
                //微信支付的地址
                String code_url = resultMap.get("code_url");

                return ResultMap.success(code_url);

            }
        }

        return null;
    }


}
