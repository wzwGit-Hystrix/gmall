package com.atguigu.gmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.SkuLsInfo;
import com.atguigu.gmall.bean.SkuLsParams;
import com.atguigu.gmall.bean.SkuLsResult;
import com.atguigu.gmall.service.ListService;
import io.searchbox.client.JestClient;

import io.searchbox.core.Index;


import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.Aggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ListServiceImpl implements ListService {
    @Autowired
    private JestClient jestClient;

    public static final String ES_INDEX = "gmall";
    public static final String ES_TYPE = "SkuInfo";

    @Override
    public void saveSkuInfo(SkuLsInfo skuLsInfo) {
        //保存数据
        Index index = new Index.Builder (skuLsInfo).index (ES_INDEX).type (ES_TYPE).id (skuLsInfo.getId ()).build ();
        try {
            jestClient.execute (index);
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        //1.定义dsl语句
        String query = makeQueryStringForSearch (skuLsParams);

        //2.准备执行动作
        Search search = new Search.Builder (query).addIndex (ES_INDEX).addType (ES_TYPE).build ();
        //2.2执行动作
        SearchResult searchResult = null;
        try {
            searchResult = jestClient.execute (search);
        } catch (IOException e) {
            e.printStackTrace ();
        }
        //返回结果集

        SkuLsResult skuLsResult = makeResultForSearch (searchResult, skuLsParams);
        return skuLsResult;
    }

    /**
     * 制作返回结果集
     *
     * @param searchResult
     * @param skuLsParams
     * @return
     */
    private SkuLsResult makeResultForSearch(SearchResult searchResult, SkuLsParams skuLsParams) {
        SkuLsResult skuLsResult = new SkuLsResult ();
        ArrayList<SkuLsInfo> infoArrayList = new ArrayList<> ();

        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits (SkuLsInfo.class);
        if (hits != null && hits.size () > 0) {
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
                SkuLsInfo skuLsInfo = hit.source;
                //如果进行通过3级id进行查询，skuname不是高亮，此时需要进行判断
                if (hit.highlight != null && hit.highlight.size () > 0) {
                    List<String> highLightList = hit.highlight.get ("skuName");
                    String skuNameHI = highLightList.get (0);
                    skuLsInfo.setSkuName (skuNameHI);
                }
                infoArrayList.add (skuLsInfo);
            }
        }
        skuLsResult.setSkuLsInfoList (infoArrayList);

        //添加总条数
        skuLsResult.setTotal (searchResult.getTotal ());

        //总页数
        long totalPages = (searchResult.getTotal () + skuLsParams.getPageSize () - 1) / skuLsParams.getPageSize ();
        skuLsResult.setTotalPages (totalPages);


        //平台属性值id，显示平台属性值和平台属性
        ArrayList<String> stringArrayList = new ArrayList<> ();
        TermsAggregation groupby_attr = searchResult.getAggregations ().getTermsAggregation ("groupby_attr");
        List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets ();
        if (buckets != null && buckets.size () > 0) {
            for (TermsAggregation.Entry bucket : buckets) {
                String valueId = bucket.getKey ();
                stringArrayList.add (valueId);
            }
        }
        skuLsResult.setAttrValueIdList (stringArrayList);
        return skuLsResult;


    }

    /**
     * 私有化方法定义dsl语句
     *
     * @param skuLsParams
     * @return
     */
    private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        //1.定义查询器，也即是最外层的{ }
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder ();
        //2.{query---bool}
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery ();
        //3-1.按照三级分类id进行过滤
        if (skuLsParams.getCatalog3Id () != null && skuLsParams.getCatalog3Id ().length () > 0) {
            // filter --- term {"term": {"catalog3Id": "61"}}
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder ("catalog3Id", skuLsParams.getCatalog3Id ());
            boolQueryBuilder.filter (termQueryBuilder);
        }
        //3-2.根据平台属性值id进行过滤
        if (skuLsParams.getValueId () != null && skuLsParams.getValueId ().length > 0) {
            for (String valueId : skuLsParams.getValueId ()) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder ("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter (termQueryBuilder);
            }
        }
        //3-3.根据skuname进行过滤
//        if (skuLsParams.getKeyword () != null && skuLsParams.getKeyword ().length () > 0) {
//            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder ("skuName", skuLsParams.getKeyword ());
//            boolQueryBuilder.must (matchQueryBuilder);
//
//            //设置高亮
//            HighlightBuilder highlightBuilder = new HighlightBuilder ();
//            highlightBuilder.preTags ("<span style=color:red>");
//            highlightBuilder.postTags ("</span>");
//            highlightBuilder.field ("skuName");
//
//            searchSourceBuilder.highlight (highlightBuilder);
//
//        }
        if (skuLsParams.getKeyword () != null && skuLsParams.getKeyword ().length () > 0) {
            /*
            {"match": {
              "skuName": "小米三代"
            }}
             */
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder ("skuName", skuLsParams.getKeyword ());
            // bool -- must
            boolQueryBuilder.must (matchQueryBuilder);

            // 设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder ();
            highlightBuilder.preTags ("<span style=color:red>");
            highlightBuilder.postTags ("</span>");
            highlightBuilder.field ("skuName");

            searchSourceBuilder.highlight (highlightBuilder);
        }

        //排序
        searchSourceBuilder.sort ("hotScore", SortOrder.DESC);

        //设置分页
        int from = skuLsParams.getPageSize () * (skuLsParams.getPageNo () - 1);
        searchSourceBuilder.from (from);
        searchSourceBuilder.size (skuLsParams.getPageSize ());

        //聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms ("groupby_attr").field ("skuAttrValueList.valueId.keyWord");
        searchSourceBuilder.aggregation (groupby_attr);

        //将bool放进query中
        searchSourceBuilder.query (boolQueryBuilder);

        String query = searchSourceBuilder.toString ();
        System.out.println (query);
        return query;
    }
}
