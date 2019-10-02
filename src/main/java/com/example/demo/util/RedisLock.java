package com.example.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public int lock(){
        long currentTime=System.currentTimeMillis();
        Map<String,Long> map=new HashMap<>();

        while(System.currentTimeMillis()-currentTime<2000){
            if(redisTemplate.opsForValue().setIfAbsent("key", String.valueOf(currentTime), Duration.ofMinutes(1L))){
                map.put("key",currentTime);
                Thread t=new Thread(()->{
                    if(Objects.equals(redisTemplate.boundValueOps("key").get(), String.valueOf(map.get("key")))){
                        Long l=redisTemplate.boundValueOps("key").getExpire();
                        assert l != null;
                        if(l.compareTo(5L)<0){
                            redisTemplate.boundValueOps("key").expire(1L, TimeUnit.MINUTES);
                        }
                    }
                });
                t.setDaemon(true);
                t.start();

                return 1;
            }
        }

        return 0;
    }

    public void unLock(){
        redisTemplate.delete("key");
    }
}