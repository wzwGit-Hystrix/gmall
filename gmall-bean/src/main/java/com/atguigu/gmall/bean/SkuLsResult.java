package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

//搜索结束之后返回的参数类型
@Data
public class SkuLsResult implements Serializable {
    List<SkuLsInfo> skuLsInfoList;
    //总条数
    long total;
    //总页数
    long totalPages;
    //平台属性值id集合，显示平台属性值，
    List<String> attrValueIdList;
}
