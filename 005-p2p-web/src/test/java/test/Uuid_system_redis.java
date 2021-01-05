package test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

/**
 * @author Luo
 * @date 2020/12/30 12:22
 * @Package test
 * class 生成全局唯一订单号的三种方法
 */


public class Uuid_system_redis {


    /**
     * 功能描述: 生成唯一的订单号
     * @Param: []
     * @Return: void
     * @Date: 2020/12/30 12:27
     */
    @Test
    public void UUIDTime(){
        //1、UUID + 时间戳
        //UUID：初始是36位,使用replaceAll方法去掉“-”号，还剩下32位
        String s = UUID.randomUUID().toString().replaceAll("-","");
        //获取时间戳
        Long time = System.currentTimeMillis();
        //刚获取的时间戳是Long类型的，要转换成String类型
        String timeString = time.toString();
        System.out.println(s+"              "+s.length());
        //时间戳是13位
        System.out.println(timeString+"              "+timeString.length());
        String UUIDTime = s + timeString;
        //UUID + 时间戳可以生成45位随机数
        System.out.println(UUIDTime+"            "+UUIDTime.length());

        //2、UUID + redis的自增
//        Long rechargeNo = redisTemplate.boundValueOps("rechargeNo").increment();
//        String rn = rechargeNo.toString();
//        System.out.println(uuid + rn);

        //3、时间戳 + redis的自增
//        Long curTime = System.currentTimeMillis();
//        System.out.println(curTime.toString() + rn);
    }

}
