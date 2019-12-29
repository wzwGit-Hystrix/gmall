package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.BaseSaleAttr;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {
    @Reference
    private ManageService manageService;

    /**
     * http://localhost:8082/spuList?catalog3Id=61
     * 查询所有的spu详情信息
     *
     * @param catalog3Id
     * @return
     */
    @RequestMapping("spuList")
    public List<SpuInfo> getSpuList(String catalog3Id) {
        return manageService.getSpuList (catalog3Id);
    }


    /**
     * http://localhost:8082/baseSaleAttrList
     * 查询基本销售属性
     *
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return manageService.getBaseSaleAttrList ();
    }


    /**
     * http://localhost:8082/saveSpuInfo
     * 保存spu信息
     *
     * @param spuInfo
     */
    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo (spuInfo);
    }
}
