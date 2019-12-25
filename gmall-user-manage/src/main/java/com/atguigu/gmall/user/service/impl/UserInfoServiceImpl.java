package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UserAddress;
import com.atguigu.gmall.bean.UserInfo;
import com.atguigu.gmall.service.UserInfoService;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> getAll() {
        return userInfoMapper.selectAll ();
    }

    @Override
    public List<UserAddress> getUserAddressByUserId(String userId) {
        Example example = new Example (UserAddress.class);
        example.createCriteria ().andEqualTo ("userId", userId);
        return userAddressMapper.selectByExample (example);
    }
}
