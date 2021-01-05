package com.lq.p2p.pay;

import com.bjpowernode.p2p.common.utils.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Luo
 * @date 2020/12/30 22:12
 * @Package com.lq.p2p.pay
 * class
 */

@Controller
@RequestMapping("/pay")
public class PayController {


    /**
     * 功能描述: 调用微信支付工程，返回code_url
     * @Param: [out_trade_no, total_fee, body]
     * @Return: java.lang.String
     * @Date: 2020/12/30 22:13
     */
    @RequestMapping("generateWxpayCodeUrl")
    public @ResponseBody String generateWxpayCodeUrl(@RequestParam("out_trade_no") String out_trade_no,
                                                     @RequestParam("total_fee") Double total_fee,
                                                     @RequestParam("body") String body) throws Exception {
        //参照微信支付的官方文档，获取code_url

        //统一下单Api
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";

        //微信支付的工具类，官方提供，提供api，xml和map集合的转换
        //微信支付官方文档上，请求的发起和响应，都是以xml标签来进行交互
        //以及参数的签名等操作

        //封装参数
        Map<String,String> paramMap = new HashMap<>();

        //如果粘贴的时候，值出现了空格，必须去除

        //公众账号ID
        paramMap.put("appid","wx8a3fcf509313fd74");
        //商户号
        paramMap.put("mch_id","1361137902");
        //随机字符串
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        //商品描述
        paramMap.put("body",body);
        //商户订单号
        paramMap.put("out_trade_no",out_trade_no);
        //标价金额，支付宝以元为单位，微信支付以分为单位
//        paramMap.put("total_fee",total_fee.toString());
        paramMap.put("total_fee","1");//交易1分钱，别到时候转多了，要钱的时候麻烦。
        //终端ip
        paramMap.put("spbill_create_ip","127.0.0.1");
        //通知地址
        paramMap.put("notify_url","www.bjpowernode.com");
        //交易类型
        paramMap.put("trade_type","NATIVE");
        //商品ID：当交易类型为NATIVE时，此参数必传
        paramMap.put("product_id",out_trade_no);

        //签名，签名一定要放到最后，要将所有的数据准备完成后，一起进行签名。
        paramMap.put("sign",WXPayUtil.generateSignature(paramMap,"367151c5fd0d50f1e34a68a802d6bbca"));

        //将map集合转换为xml数据
        String requestDataXml = WXPayUtil.mapToXml(paramMap);


        //发送请求，获取code_url
        //微信支付的要求使用xml来进行交互
        String result = HttpClientUtils.doPostByXml(url, requestDataXml);
        //该结果集包含code_url，但是它响应回来的是xml格式的数据

        System.out.println("result结果："+result);
        /**
         * <xml><return_code><![CDATA[SUCCESS]]></return_code>
         * <return_msg><![CDATA[OK]]></return_msg>
         * <appid><![CDATA[wx8a3fcf509313fd74]]></appid>
         * <mch_id><![CDATA[1361137902]]></mch_id>
         * <nonce_str><![CDATA[6uYBVD0J5np3ja1Y]]></nonce_str>
         * <sign><![CDATA[34AB93B3D5F2B81D273ACCBC16A6DDEE]]></sign>
         * <result_code><![CDATA[SUCCESS]]></result_code>
         * <prepay_id><![CDATA[wx01105248917589c1f351419ca9c1320000]]></prepay_id>
         * <trade_type><![CDATA[NATIVE]]></trade_type>
         * <code_url><![CDATA[weixin://wxpay/bizpayurl?pr=jup2P0G00]]></code_url>
         * </xml>
         */
        //根据code_url生成微信支付二维码
        //weixin://wxpay/bizpayurl?pr=jup2P0G00根据微信支付协议生成的地址
        //将xml数据，转换map

        return result;
    }

}
