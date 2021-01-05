package com.bjpowernode.p2p.service.loan;


import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = BidInfoService.class, version = "1.0.0", timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Override
    public Double queryAllBidMoney() {
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
        if (!ObjectUtils.allNotNull(allBidMoney)) {
            synchronized (this) {
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
                if (!ObjectUtils.allNotNull(allBidMoney)) {
                    // 从数据库中获取数据
                    allBidMoney = bidInfoMapper.selectAllBidMoney();
                    // 将平头投资总额保存到redis缓存中
                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY, allBidMoney);
                }
            }
        }

        return allBidMoney;
    }


    /**
     * 根据产品ID查询最近十条投资记录
     *
     * @param paramMap
     * @return
     */
    @Override
    public List<BidInfo> querBidInfoUserByLoanId(Map<String, Object> paramMap) {
        return bidInfoMapper.selectBidInfoUserByLoanId(paramMap);
    }

    /**
     * 功能描述: 根据当前已经满标的产品id，查询出它对应的投资列表
     * @Param: [loanId]
     * @Return: java.util.List<com.bjpowernode.p2p.model.loan.BidInfo>
     * @Date: 2020/12/29 17:57
     */
    @Override
    public List<BidInfo> queryBidInfosByLoanId(Integer loanId) {
        return bidInfoMapper.selectBidInfosByLoanId(loanId);
    }
}
