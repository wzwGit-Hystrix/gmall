package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SpuImage;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SkuManageController {
    @Reference
    private ManageService manageService;
    @Reference
    private ListService listService;


    /**
     * 根据spuid查询所有的spu图片，并且在sku添加界面回显
     * http://localhost:8082/spuImageList?spuId=65
     *
     * @param spuImage
     * @return
     */
    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return manageService.getSpuImageList (spuImage);
    }


    /**
     * 根据spuid查询销售属性和销售属性值
     * http://localhost:8082/spuSaleAttrList?spuId=65
     *
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        return manageService.getSpuSaleAttrList (spuId);
    }

    //http://localhost:8082/saveSkuInfo

    /**
     * 保存sku信息
     *
     * @param skuInfo
     */
    @RequestMapping("saveSkuInfo")
    public void saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        manageService.saveSkuInfo (skuInfo);

    }

    /**
     * 商品上传成功之后保存到elastic中
     *
     * @param skuId
     */
    @RequestMapping("onSale")
    public void onSale(String skuId) {
        SkuLsInfo skuLsInfo = new SkuLsInfo ();
        //获取skuinfo信息
        SkuInfo skuInfo = manageService.getSkuInfo (skuId);

        //属性拷贝
        BeanUtils.copyProperties (skuInfo, skuLsInfo);

        //保存skulsinfo信息
        listService.saveSkuInfo (skuLsInfo);
    }
}
