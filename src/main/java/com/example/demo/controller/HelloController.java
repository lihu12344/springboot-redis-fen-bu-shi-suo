package com.example.demo.controller;

import com.example.demo.util.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class HelloController {

    @Autowired
    private RedisLock lock;

    @RequestMapping("/hello")
    public String hello(){
        Thread t=new Thread(()->{
            if(lock.lock()!=0){
                try{
                    System.out.println(Thread.currentThread().getId()+" "+System.currentTimeMillis());
                    try{
                        Thread.sleep(2000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    lock.unLock();
                }
            }
        });
        ExecutorService executorService=Executors.newFixedThreadPool(10);
        for(int i=0;i<100;i++){
            executorService.submit(t);
        }

        return "success";
    }
}
