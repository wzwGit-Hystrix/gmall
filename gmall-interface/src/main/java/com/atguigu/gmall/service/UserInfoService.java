package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;

import java.util.List;

public interface UserInfoService {
    /**
     * 获取所有用户信息
     *
     * @return
     */
    List<UserInfo> getAll();
    /**
     * 根据用户id查询收货地址
     */
    List<UserAddress>getUserAddressByUserId(String userId);
}
