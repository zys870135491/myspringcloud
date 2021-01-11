package com.zys.springcloud.service;

import com.zys.springcloud.entities.CommonResult;
import com.zys.springcloud.entities.Payment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "sentinel-nacos-payment-provider")
public interface TokenService {

    @GetMapping(value = "/paymentToken/{id}")
    public CommonResult<Payment> paymentToken(@PathVariable("id") Long id);

}
