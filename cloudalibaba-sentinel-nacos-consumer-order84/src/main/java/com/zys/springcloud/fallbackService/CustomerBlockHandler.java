package com.zys.springcloud.fallbackService;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zys.springcloud.entities.*;
import org.springframework.web.bind.annotation.PathVariable;

public class CustomerBlockHandler {

    //一定要和controller层方法的参数保持一致(@PathVariable("id") Long id 一定要加)，方法一定要加static
    public static CommonResult handleException(@PathVariable("id") Long id,BlockException exception) {
        return new CommonResult(2020, "自定义限流处理信息....CustomerBlockHandler");

    }
}

