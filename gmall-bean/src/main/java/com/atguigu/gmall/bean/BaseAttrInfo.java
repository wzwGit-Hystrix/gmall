package com.atguigu.gmall.bean;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
public class BaseAttrInfo implements Serializable {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)//sql中获取主键自增
    private String id;
    @Column
    private String attrName;
    @Column
    private String catalog3Id;
    @Transient //表示数据库中没有此字段，但是业务中需要
    private List<BaseAttrValue> attrValueList;
}
