package com.atguigu.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.service.UserInfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {
    @Reference
    private UserInfoService userInfoService;

    /**
     * 根据用户id查询收货地址
     *
     * @param userId
     * @return
     */
    @RequestMapping("trade")
    public List<UserAddress> trade(String userId) {
        return userInfoService.getUserAddressByUserId (userId);
    }
}
