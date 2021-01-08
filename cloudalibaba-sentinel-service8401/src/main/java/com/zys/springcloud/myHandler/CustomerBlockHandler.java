package com.zys.springcloud.myHandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.zys.springcloud.entities.*;

public class CustomerBlockHandler {

    public static CommonResult handleException(BlockException exception) {
        return new CommonResult(2020, "自定义限流处理信息....CustomerBlockHandler");

    }
}
