package com.atguigu.gmall.item.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.SkuInfo;
import com.atguigu.gmall.bean.SkuSaleAttrValue;
import com.atguigu.gmall.bean.SpuSaleAttr;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
public class ItemController {
    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable String skuId, HttpServletRequest request) {
        //将商品的图片列表封装进skuinfo里边
        SkuInfo skuInfo = manageService.getSkuInfo (skuId);
        request.setAttribute ("skuInfo", skuInfo);

        //查询销售属性-销售属性值并且锁定
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku (skuInfo);
        request.setAttribute ("spuSaleAttrList", spuSaleAttrList);

        //查询skuid和销售属性值的数据集合
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu (skuInfo.getSpuId ());

        //拼接字符串
        String key = "";
        HashMap<String, String> map = new HashMap<> ();
        if (skuSaleAttrValueListBySpu != null && skuSaleAttrValueListBySpu.size () > 0) {
            for (int i = 0; i < skuSaleAttrValueListBySpu.size (); i++) {
                SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get (i);

                if (key.length () > 0) {
                    key += "|";
                }

                key += skuSaleAttrValue.getSaleAttrValueId ();
                // 什么时候停止拼接
                if ((i + 1) == skuSaleAttrValueListBySpu.size () || !skuSaleAttrValue.getSkuId ().equals (skuSaleAttrValueListBySpu.get (i + 1).getSkuId ())) {
                    map.put (key, skuSaleAttrValue.getSkuId ());
                    // 清空key
                    key = "";
                }
            }
        }
        // 将map 转换json
        String valuesSkuJson = JSON.toJSONString (map);
        request.setAttribute ("valuesSkuJson", valuesSkuJson);

        return "item";
    }

}
