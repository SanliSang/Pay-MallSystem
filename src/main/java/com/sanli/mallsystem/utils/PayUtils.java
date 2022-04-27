package com.sanli.mallsystem.utils;

import java.util.Random;

public class PayUtils {
    // TODO 高级一点的采用分布式唯一id（分布式唯一id则可以保证多台系统调用的时候也能获取到唯一的标识号）
    // 这里写的太简单多了
    public static Long generatorOrderNo(){
        long timestamp = System.currentTimeMillis();
        timestamp = timestamp >> 8; // 除以2的八次方
        return timestamp + new Random().nextInt(99999); // 变换时间 + 5位随机数
    }
}
