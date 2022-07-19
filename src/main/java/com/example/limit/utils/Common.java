package com.example.limit.utils;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 21:23
 */
public class Common {

    /** 默认接口请求上限 */
    public static final int REQUEST_CAP = 1000;
    /** 默认时间间隔 */
    public static final long MILLISECOND = 1;
    /** 限流的redis键 */
    public static final String LIMIT_REDIS_KEY = "limit.redis.key";


}
