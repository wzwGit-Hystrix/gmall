package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.*;

import java.util.List;

public interface ManageService {
    /**
     * 查询一级分类信息
     *
     * @return
     */
    List<BaseCatalog1> getCatalog1();

    /**
     * 根据一级信息id查询二级分类信息
     *
     * @param baseCatalog2
     * @return
     */
    List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2);

    /**
     * 根据二级信息查询三级分类信息
     *
     * @param baseCatalog3
     * @return
     */
    List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3);

    /**
     * 通过三级信息查询baseinfo
     *
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> attrInfoList(BaseAttrInfo baseAttrInfo);

    /**
     * 接受前端传值，保存属性值
     *
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据id查询是否有这个info的信息
     *
     * @param attrId
     * @return
     */
    BaseAttrInfo getBaseAttrInfo(String attrId);

    /**
     * 查询所有的商品属性信息
     *
     * @param catalog3Id
     * @return
     */
    List<SpuInfo> getSpuList(String catalog3Id);

    /**
     * 查询基本销售属性
     *
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 保存spu信息
     *
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);


    /**
     * 根据spuid查询所有的spu图片，并且进行回显
     *
     * @param
     * @return
     */
    List<SpuImage> getSpuImageList(SpuImage spuImage);

    /**
     * 根据spuid查询销售属性和销售属性值
     *
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    /**
     * 通过三级属性id查询平台属性和平台属性值
     *
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoList(String catalog3Id);

    /**
     * 保存sku信息
     *
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 通过skuId查询skuInfo
     *
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(String skuId);


    /**
     * 查询销售属性和销售属性值并且锁定
     *
     * @param skuInfo
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    /**
     * 查询skuid和销售属性值的集合
     *
     * @param spuId
     * @return
     */
    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    /**
     * 通过平台属性值id获取平台属性和平台属性值信息
     *
     * @param attrValueIdList
     * @return
     */
    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
