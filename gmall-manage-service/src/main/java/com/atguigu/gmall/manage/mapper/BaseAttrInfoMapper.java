package com.atguigu.gmall.manage.mapper;

import com.atguigu.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    /**
     * 通过三级属性id查询平台属性和平台属性值
     *
     * @param catalog3Id
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoListByCatalog3Id(String catalog3Id);

    /**
     * 根据传入的id拼接的字符串查询平台属性和平台属性值
     *此处必须要用@Param注解否则${ }无法识别。
     * @param valueIds
     * @return
     */
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String valueIds);
}
