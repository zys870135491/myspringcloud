package com.zys.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient //服务注册
@SpringBootApplication
public class OrderNacosMain90
{
    public static void main(String[] args)
    {
        SpringApplication.run(OrderNacosMain90.class,args);
    }
}


