package com.atguigu.gmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //通过注解使成为.xml文件
public class RedisConfig {
    /**
     * 1.获取配置文件中的host，post，timeout信息
     * 2.将RedisUtil放进spring容器中进行管理
     */
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.timeOut:10000}")
    private int timeOut;

    @Bean
    public RedisUtil getRedisUtil(){
        //如果没有host则返回一个空的文本
        if("disabled".equals (host)){
            return null;
        }
        RedisUtil redisUtil = new RedisUtil ();
        redisUtil.initJedisPool (host,port,timeOut);
        return  redisUtil;
    }

}
