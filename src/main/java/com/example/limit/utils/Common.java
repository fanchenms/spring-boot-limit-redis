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
    /** 限流的redis键，固定时间窗口算法redis键前缀 */
    public static final String LIMIT_REDIS_KEY_FIXED_TIME = "limit:redis:key:fixedTimeWindow";
    /** 限流的redis键，滑动时间窗口算法redis键前缀 */
    public static final String LIMIT_REDIS_KEY_SLIDING_TIME = "limit:redis:key:slidingTimeWindow";
    /** 限流的redis键，令牌同算法redis键 */
    public static final String LIMIT_REDIS_KEY_TOKEN_BUCKET = "limit:redis:key:tokenBucket";


}
