package com.atguigu.gmall.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    /**
     * 1.创建连接池工厂
     * 2.获取jedis
     */
    private JedisPool jedisPool;

    //初始化连接池工厂
    public void initJedisPool(String host, int port, int timeOut) {
        //初始化参数配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig ();

        //设置最大连接数
        jedisPoolConfig.setMaxTotal (200);

        //设置允许排队等待
        jedisPoolConfig.setBlockWhenExhausted (true);

        //设置等待时间
        jedisPoolConfig.setMaxWaitMillis (10 * 1000);

        //设置最大剩余数
        jedisPoolConfig.setMinIdle (10);

        //表示获取到连接时，自检以下判断连接数是否可用
        jedisPoolConfig.setTestOnBorrow (true);

        jedisPool = new JedisPool (jedisPoolConfig, host, port, timeOut);
    }

    // 获取Jedis 方法
    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource ();
        return jedis;
    }
}
