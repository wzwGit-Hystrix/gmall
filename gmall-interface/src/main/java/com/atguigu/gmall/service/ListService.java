package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;

public interface ListService {
    /**
     * 商品上架保存suinfo
     *
     * @param skuLsInfo
     */
    public void saveSkuInfo(SkuLsInfo skuLsInfo);

    /**
     * 根据传入搜索参数，返回搜索结果
     *
     * @param skuLsParams
     * @return
     */
    public SkuLsResult search(SkuLsParams skuLsParams);
}
