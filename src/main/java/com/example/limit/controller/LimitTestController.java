package com.example.limit.controller;

import com.example.limit.annotation.RequestLimit;
import com.example.limit.enums.LimitTypeEnum;
import com.example.limit.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 20:27
 */
@RequestLimit(type = LimitTypeEnum.TOKEN_BUCKET)
//@RequestLimit(type = LimitTypeEnum.LEAKY_BUCKET)
@RestController
public class LimitTestController {

    @GetMapping("/hello")
    public String testHello() {
        return "hello";
    }

    /** 测试限流 */
    @RequestLimit(requestCap = 1000, time = 1)
    @GetMapping("/limit")
    public R testLimit() {
        return R.success("访问正常！");
    }

    /** 测试限流，固定时间窗口 */
    @RequestLimit(requestCap = 3, time = 1, type = LimitTypeEnum.FIXED_TIME_WINDOW)
    @GetMapping("/limit2/1")
    public R testLimit2Fixed() {
        return R.success("访问正常！");
    }

    /** 测试限流,滑动时间窗口 */
    @RequestLimit(requestCap = 3, time = 1)
    @GetMapping("/limit2/2")
    public R testLimit2() {
        return R.success("访问正常！");
    }

    /** 测试限流，固定时间窗口 */
    @RequestLimit(requestCap = 5, time = 10, type = LimitTypeEnum.FIXED_TIME_WINDOW)
    @GetMapping("/limit3/1")
    public R testLimit3Fixed() {
        return R.success("访问正常！");
    }

    /** 测试限流,滑动时间窗口 */
    @RequestLimit(requestCap = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @GetMapping("/limit3/2")
    public R testLimit3() {
        return R.success("访问正常！");
    }


    /** 测试限流,令牌桶 */
    @RequestLimit(type = LimitTypeEnum.TOKEN_BUCKET)
    @GetMapping("/limit4")
    public R testLimitToken() {
        return R.success("访问正常！");
    }

    /** 测试限流,令牌桶 */
    @RequestLimit(type = LimitTypeEnum.TOKEN_BUCKET)
    @GetMapping("/limit5")
    public R testLimitToken2() {
        return R.success("访问正常！");
    }

}
