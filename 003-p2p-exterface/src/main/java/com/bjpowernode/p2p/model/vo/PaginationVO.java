package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * PackageName:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date 2020/12/19 10:21
 * @author: 动力节点
 */

public class PaginationVO<T> implements Serializable {

    // 总条数
    private Long total;

    // 产品列表信息
    private List<T> dataList;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }
}
