package com.example.limit.enums;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/20 - 2:42
 * 限流方式
 */
public enum LimitTypeEnum {
    /** 固定时间窗口算法 */
    FIXED_TIME_WINDOW(1, "固定时间窗口算法"),
    /** 滑动时间窗口算法 */
    SLIDING_TIME_WINDOW(2, "滑动时间窗口算法"),
    /** 令牌桶算法 */
    TOKEN_BUCKET(3, "令牌桶算法"),
    /** 令牌桶算法 */
    LEAKY_BUCKET(4, "漏桶算法"),
    ;

    private final int value;
    private final String describe;

    LimitTypeEnum(int value, String describe) {
        this.value = value;
        this.describe = describe;
    }

    public int getValue() {
        return value;
    }

    public String getDescribe() {
        return describe;
    }
}
