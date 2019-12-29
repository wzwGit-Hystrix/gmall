package com.atguigu.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.SpuInfo;
import com.atguigu.gmall.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
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
}
