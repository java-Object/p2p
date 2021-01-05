package com.bjpowernode.p2p.common.utils;

import java.util.HashMap;

/**
 * @author Luo
 * @date 2020/12/21 19:47
 * @Package com.bjpowernode.p2p.common.utils
 * class  返回查询的值是否成功
 */
public class ResultMap<T> extends HashMap<String, Object> {


    /**
     * 功能描述: 封装，公共的返回方法，成功和失败，自定义
     * @Param: []
     * @Return: com.bjpowernode.p2p.common.utils.ResultMap
     * @Date: 2020/12/27 11:28
     */
    public static ResultMap success(){
        ResultMap resultMap = new ResultMap();
        resultMap.put("success",true);
        resultMap.put("msg","请求成功");
        return resultMap;
    }


    /**
     * 功能描述: 返回请求成功
     * @Param: [data] 传回前端的参数
     * @Return: com.bjpowernode.p2p.common.utils.ResultMap
     * @Date: 2020/12/25 11:18
     */
    public static ResultMap success(Object data){

        ResultMap resultMap = new ResultMap();
        resultMap.put("success",true);
        resultMap.put("msg","请求成功");
        resultMap.put("data",data);
        return resultMap;
    }

    /**
     * 功能描述: 返回请求成功
     * @Param: [data] 传回前端的参数
     * @Return: com.bjpowernode.p2p.common.utils.ResultMap
     * @Date: 2020/12/25 11:18
     */
    public static ResultMap success(Double money){

        ResultMap resultMap = new ResultMap();
        resultMap.put("success",true);
        resultMap.put("msg","请求成功");
        resultMap.put("money",money);
        return resultMap;
    }


    /**
     * 功能描述: 获取验证码的成功
     * @Param: [messageCode]
     * @Return: com.bjpowernode.p2p.common.utils.ResultMap
     * @Date: 2020/12/24 15:10
     */
    public static ResultMap resultSuccess(String messageCode){
        ResultMap resultMap = new ResultMap();
        resultMap.put("code","1");
        resultMap.put("message","");
        resultMap.put("success",true);
        resultMap.put("messageCode",messageCode);
        return resultMap;
    }


    /**
     * 功能描述: 验证通过
     *
     * @Param: []
     * @Return: com.bjpowernode.p2p.common.utils.ResultMap
     * @Date: 2020/12/21 19:52
     */
    public static ResultMap resultSuccess() {
        ResultMap resultMap = new ResultMap();
        resultMap.put("code", "1");
        resultMap.put("message", "");
        resultMap.put("success", true);

        return resultMap;
    }

    /**
     * 功能描述: 返回失败
     *
     * @Param: []
     * @Return: com.bjpowernode.p2p.common.utils.ResultMap
     * @Date: 2020/12/21 19:55
     */
    public static ResultMap resultError(String msg) {
        ResultMap resultMap = new ResultMap();
        resultMap.put("code", "-1");
        resultMap.put("message", msg);
        resultMap.put("success", false);

        return resultMap;
    }
}
