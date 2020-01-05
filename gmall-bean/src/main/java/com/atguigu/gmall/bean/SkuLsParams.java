package com.atguigu.gmall.bean;

import lombok.Data;

import java.io.Serializable;
//进行搜索时传入的参数
@Data
public class SkuLsParams implements Serializable {
    //根据skuname进行查询
    String  keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo=1;

    int pageSize=20;
}
