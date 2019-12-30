package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {
    @Reference
    private ManageService manageService;


    /**
     * http://localhost:8082/getCatalog1
     * 查询一级分类信息
     *
     * @return
     */
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1() {
        return manageService.getCatalog1 ();
    }


    /**
     * http://localhost:8082/getCatalog2?catalog1Id=11
     * 根据前端传过来的一级分类信息id查询二级分类信息
     *
     * @param catalog1Id
     * @param baseCatalog2
     * @return
     */
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id, BaseCatalog2 baseCatalog2) {
        return manageService.getCatalog2 (baseCatalog2);
    }


    /**
     * http://localhost:8082/getCatalog3?catalog2Id=38
     * 根据前端传过来的二级分类信息id查询三级分类信息
     *
     * @param catalog2Id
     * @param baseCatalog3
     * @return
     */
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id, BaseCatalog3 baseCatalog3) {
        return manageService.getCatalog3 (baseCatalog3);
    }

    /**
     * http://localhost:8082/attrInfoList?catalog3Id=356
     * 通过三级信息查询baseinfo
     *
     * @param catalog3Id
     * @param baseAttrInfo
     * @return
     */
    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo> attrInfoList(String catalog3Id, BaseAttrInfo baseAttrInfo) {
        return manageService.getAttrInfoList (catalog3Id);
    }


    /**
     * http://localhost:8082/saveAttrInfo
     * 接受前台数据保存属性值
     */
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {
        manageService.saveAttrInfo (baseAttrInfo);
    }


    /**
     * http://localhost:8082/getAttrValueList?attrId=37
     * 通过attrid进行回显
     *
     * @param attrId
     * @return
     */
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue> getAttrValueList(String attrId) {
        //功能上满足
        //return manageService.getAttrValueList(attrId);
//但是从业务上边，应该先查询info里边是否存在，在查询是否有着一个value
        BaseAttrInfo baseAttrInfo = manageService.getBaseAttrInfo (attrId);
        return baseAttrInfo.getAttrValueList ();
    }


}
