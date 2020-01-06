package com.atguigu.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.ListService;
import com.atguigu.gmall.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String list(SkuLsParams skuLsParams, HttpServletRequest request) {
        //设置每页显示的条数
       skuLsParams.setPageSize (3);
        //根据参数返回sku列表
        SkuLsResult skuLsResult = listService.search (skuLsParams);
        //获取所有的商品信息
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList ();
        //显示平台属性和平台属性值信息,获取平台属性值id
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList ();
        //通过平台属性值id的集合获取平台属性信息
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList (attrValueIdList);
        //制作urlParam参数
        String urlParam = makeUrlParam (skuLsParams);

        //声明一个保存面包屑的集合
        ArrayList<BaseAttrValue> baseAttrValueArrayList = new ArrayList<> ();

        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator (); iterator.hasNext (); ) {
            BaseAttrInfo baseAttrInfo = iterator.next ();
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList ();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                if (skuLsParams.getValueId () != null && skuLsParams.getValueId ().length > 0) {
                    for (String valueId : skuLsParams.getValueId ()) {
                        //当url中的valueId与平台属性中的valueId一样时，将整体平台属性移除
                        if (valueId.equals (baseAttrValue.getId ())) {
                            iterator.remove ();

                            //组装面包屑，形式是{属性名：属性值名}
                            //此时使用baseattrvalue对象进行封装
                            BaseAttrValue baseAttrValueed = new BaseAttrValue ();
                            baseAttrValueed.setValueName (baseAttrInfo.getAttrName () + ":" + baseAttrValue.getValueName ());
                            String newUrlParam = makeUrlParam (skuLsParams, valueId);
                            baseAttrValueed.setUrlParam (newUrlParam);
                            //将每一个面包屑都放进集合中
                            baseAttrValueArrayList.add (baseAttrValueed);

                        }
                    }
                }
            }

        }
        //添加分页数据
        request.setAttribute ("pageNo", skuLsParams.getPageNo ());
        request.setAttribute ("totalPages", skuLsResult.getTotalPages ());


        request.setAttribute ("keyword", skuLsParams.getKeyword ());
        request.setAttribute ("baseAttrValueArrayList", baseAttrValueArrayList);
        request.setAttribute ("urlParam", urlParam);

        request.setAttribute ("baseAttrInfoList", baseAttrInfoList);
        request.setAttribute ("skuLsInfoList", skuLsInfoList);


        return "list";
    }

    /**
     * 制作urlParam参数
     *
     * @param skuLsParams
     * @return
     */
    private String makeUrlParam(SkuLsParams skuLsParams, String... excludeValueIds) {
        String urlParam = "";
        if (skuLsParams.getKeyword () != null && skuLsParams.getKeyword ().length () > 0) {
            urlParam += "keyword=" + skuLsParams.getKeyword ();
        }
        if (skuLsParams.getCatalog3Id () != null && skuLsParams.getCatalog3Id ().length () > 0) {
            urlParam += "catalog3Id=" + skuLsParams.getCatalog3Id ();
        }
        //判断用户是否使用了平台属性值进行检索
        if (skuLsParams.getValueId () != null && skuLsParams.getValueId ().length > 0) {
            for (String valueId : skuLsParams.getValueId ()) {
                if (excludeValueIds != null && excludeValueIds.length > 0) {
                    //此时获取对象中的第一个数据，因为每次点击关闭面包屑时都是一个一个进行关闭的
                    String excludeValueId = excludeValueIds[0];
                    if (excludeValueId.equals (valueId)) {
                        //此时跳出本次for循环，不在添加valueid到url中
                        continue;

                    }
                }
                urlParam += "&valueId=" + valueId;

            }
        }
        return urlParam;
    }
}
