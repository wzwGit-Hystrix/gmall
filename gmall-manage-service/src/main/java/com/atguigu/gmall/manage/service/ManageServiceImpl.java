package com.atguigu.gmall.manage.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.config.RedisUtil;
import com.atguigu.gmall.manage.constant.ManageConst;
import com.atguigu.gmall.manage.mapper.*;
import com.atguigu.gmall.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisUtil redisUtil;


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

    @Override
    public List<SpuInfo> getSpuList(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo ();
        spuInfo.setCatalog3Id (catalog3Id);
        return spuInfoMapper.select (spuInfo);
    }

    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectAll ();
    }

    @Override
    @Transactional
    public void saveSpuInfo(SpuInfo spuInfo) {
        //此时要保存进spuinfo ，image，saleattr，saleattrvalue
        spuInfoMapper.insertSelective (spuInfo);
        List<SpuImage> spuImageList = spuInfo.getSpuImageList ();
        //保存图片
        if (spuImageList != null && spuImageList.size () > 0) {
            for (SpuImage spuImage : spuImageList) {
                spuImage.setSpuId (spuInfo.getId ());
                spuImageMapper.insertSelective (spuImage);
            }
        }

        //保存销售属性
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList ();
        if (spuSaleAttrList != null && spuSaleAttrList.size () > 0) {
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                spuSaleAttr.setSpuId (spuInfo.getId ());
                spuSaleAttrMapper.insertSelective (spuSaleAttr);

                //保存销售属性值
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList ();
                if (spuSaleAttrValueList != null && spuSaleAttrValueList.size () > 0) {
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        spuSaleAttrValue.setSpuId (spuInfo.getId ());
                        spuSaleAttrValueMapper.insertSelective (spuSaleAttrValue);
                    }
                }

            }
        }

    }

    @Override
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return spuImageMapper.select (spuImage);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {

        return spuSaleAttrMapper.selectSpuSaleAttrList (spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoList(String catalog3Id) {
        return baseAttrInfoMapper.selectBaseAttrInfoListByCatalog3Id (catalog3Id);
    }

    @Override
    @Transactional
    public void saveSkuInfo(SkuInfo skuInfo) {
        //直接报讯skuinfo内容
        skuInfoMapper.insertSelective (skuInfo);
        //保存sku图片
        List<SkuImage> skuImageList = skuInfo.getSkuImageList ();
        if (skuImageList != null && skuImageList.size () > 0) {
            for (SkuImage skuImage : skuImageList) {
                skuImage.setSkuId (skuInfo.getId ());
                skuImageMapper.insertSelective (skuImage);
            }
        }

        //保存平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList ();
        if (skuAttrValueList != null && skuAttrValueList.size () > 0) {
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                skuAttrValue.setSkuId (skuInfo.getId ());
                skuAttrValueMapper.insertSelective (skuAttrValue);
            }
        }

        //保存销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList ();
        if (skuSaleAttrValueList != null && skuSaleAttrValueList.size () > 0) {
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                skuSaleAttrValue.setSkuId (skuInfo.getId ());
                skuSaleAttrValueMapper.insertSelective (skuSaleAttrValue);
            }
        }

    }

    @Override
    public SkuInfo getSkuInfo(String skuId) {
        return getSkuInfoByRedisson (skuId);
    }

    //使用redisson解决分布式锁
    private SkuInfo getSkuInfoByRedisson(String skuId) {
        SkuInfo skuInfo = null;
        Jedis jedis = null;
        RLock myLock = null;
        try {
            //获取jedis
            jedis = redisUtil.getJedis ();
            //设置key值
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            String skuJson = jedis.get (skuKey);
            if (!jedis.exists (skuKey)) {
                System.out.println ("redis中没有数据========================");
                //没有数据
                Config config = new Config ();
                //设置redis节点
                config.useSingleServer ().setAddress ("redis://192.168.1.66:6379");

                //创建redisson实例
                RedissonClient redisson = Redisson.create (config);

                //创建锁
                myLock = redisson.getLock ("myLock");
                //上锁
                boolean res = myLock.tryLock (100, 10, TimeUnit.SECONDS);
                if (res) {
                    System.out.println ("数据库中查询存入redis中");
                    //数据库中进行查询
                    skuInfo = getSkuInfoDB (skuId);
                    //将数据存入数据库
                    jedis.setex (skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString (skuInfo));
                    return skuInfo;
                }
            } else {
                //缓存中有数据
                System.out.println ("直接查询redis-----------------------");
                skuInfo = JSON.parseObject (skuJson, SkuInfo.class);
                return skuInfo;
            }
        } catch (InterruptedException e) {
            e.printStackTrace ();
        } finally {
            //关闭缓存
            if (jedis != null) {
                jedis.close ();
            }
            //关闭锁
            if (myLock != null) {
                myLock.unlock ();
            }
        }
        return getSkuInfoDB (skuId);
    }

    //通过set命令设置set锁
    private SkuInfo getSkuInfoByRedisSet(String skuId) {
        Jedis jedis = null;
        SkuInfo skuInfo = null;

        try {
            //获取jedis
            jedis = redisUtil.getJedis ();

            //定义key值
            String skuKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
            String skuJson = jedis.get (skuKey);
            //Boolean exists = jedis.exists (skuKey);
            if (skuJson == null || skuJson.length () == 0) {
                //redis中没有数据
                System.out.println ("------------------redis中没有数据");
                //准备加锁，定义锁的key
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;

                //定义key锁定的值
                String token = UUID.randomUUID ().toString ().replace ("-", "");
                //执行加锁命令
                String lockKey = jedis.set (skuLockKey, token, "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                if ("OK".equals (lockKey)) {
                    System.out.println ("=======================上锁成功");
                    //数据库中获取数据
                    skuInfo = getSkuInfoDB (skuId);
                    //将数据放到缓存中
                    jedis.setex (skuKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString (skuInfo));
                    // 解锁：
                    // jedis.del(skuKey); lua 脚本：
                    String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                    jedis.eval (script, Collections.singletonList (skuLockKey), Collections.singletonList (token));
                    return skuInfo;
                } else {
                    //加锁失败，线程进入睡眠状态
                    Thread.sleep (1000);
                    //醒来后再次执行方法
                    return getSkuInfo (skuId);
                }

            } else {
                //缓存中有数据，直接将数据转化为对象
                skuInfo = JSON.parseObject (skuJson, SkuInfo.class);
                System.out.println ("-----------------直接查询redis");
                return skuInfo;
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            //关闭缓存
            if (jedis != null) {
                jedis.close ();
            }
        }

        return getSkuInfoDB (skuId);
    }


    /**
     * 抽取方法直接查询数据库
     *
     * @param skuId
     * @return
     */
    private SkuInfo getSkuInfoDB(String skuId) {
        //测试jedis连接
//        try {
//            Jedis jedis = redisUtil.getJedis();
//            jedis.set("test","text_value" );
//        }catch (JedisConnectionException e){
//            e.printStackTrace();
//        }
        // System.out.println ("开始查询数据库---------------");
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey (skuId);
        SkuImage skuImage = new SkuImage ();
        skuImage.setSkuId (skuId);
        List<SkuImage> skuImageList = skuImageMapper.select (skuImage);
        //skuattrvlue
        SkuAttrValue skuAttrValue = new SkuAttrValue ();
        skuAttrValue.setSkuId (skuId);
        skuInfo.setSkuAttrValueList (skuAttrValueMapper.select (skuAttrValue));
        skuInfo.setSkuImageList (skuImageList);
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku (skuInfo.getId (), skuInfo.getSpuId ());
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu (spuId);
    }

    @Override
    public List<BaseAttrInfo> getAttrList(List<String> attrValueIdList) {
        //使用方法将集合转化为字符串，并且之间用，进行分割
       // String attrValueIds = StringUtils.join (attrValueIdList.toArray (), ",");
        String attrValueIds  = org.apache.commons.lang3.StringUtils.join(attrValueIdList.toArray(), ",");

        System.out.println ("-------------输入的字符串" + attrValueIds);
        //重写mapper
        return baseAttrInfoMapper.selectAttrInfoListByIds (attrValueIds);
    }


}
