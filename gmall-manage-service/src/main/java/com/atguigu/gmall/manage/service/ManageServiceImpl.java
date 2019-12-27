package com.atguigu.gmall.manage.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseCatalog1> getCatalog1() {
        return baseCatalog1Mapper.selectAll ();
    }

    @Override
    public List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2) {
        return baseCatalog2Mapper.select (baseCatalog2);
    }

    @Override
    public List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3) {
        return baseCatalog3Mapper.select (baseCatalog3);
    }

    @Override
    public List<BaseAttrInfo> attrInfoList(BaseAttrInfo baseAttrInfo) {
        return baseAttrInfoMapper.select (baseAttrInfo);
    }

    @Override
    @Transactional //此处对两张表进行操作，因此开启事务
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //判断前端传值中是否有infoId
        if (baseAttrInfo.getId () != null && baseAttrInfo.getId ().length () > 0) {
            //此时有infoId进行修改操作
            baseAttrInfoMapper.updateByPrimaryKeySelective (baseAttrInfo);

        } else {
            //此时没有attrId，直接保存info里边的属性
            baseAttrInfoMapper.insertSelective (baseAttrInfo);
        }

        //此时进行修改操作，将valueList中原有的值进行删除，之后重新添加
        BaseAttrValue baseAttrValue1 = new BaseAttrValue ();
        baseAttrValue1.setAttrId (baseAttrInfo.getId ());
        baseAttrValueMapper.delete (baseAttrValue1);

        //将info里边的集合取出
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList ();
        //判断集合中是否有值,先判断是否为空，如果先对size进行判断会出现空指针的风险，
        // 判断集合不为null之后仍然判断size是因为空集的存在，空集是不为null的但是size为零
        if (attrValueList != null && attrValueList.size () > 0) {
            for (BaseAttrValue baseAttrValue : attrValueList) {
                //获取baseAttrInfo的id值，作为baseAttrValue的attrId存入
                baseAttrValue.setAttrId (baseAttrInfo.getId ());
                baseAttrValueMapper.insertSelective (baseAttrValue);
            }
        }
    }

    @Override
    public BaseAttrInfo getBaseAttrInfo(String attrId) {
        BaseAttrInfo baseAttrInfo = baseAttrInfoMapper.selectByPrimaryKey (attrId);
        BaseAttrValue baseAttrValue = new BaseAttrValue ();
        baseAttrValue.setAttrId (attrId);
        List<BaseAttrValue> selectList = baseAttrValueMapper.select (baseAttrValue);
        baseAttrInfo.setAttrValueList (selectList);
        return baseAttrInfo;
    }
}
