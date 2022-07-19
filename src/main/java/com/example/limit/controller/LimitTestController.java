package com.example.limit.controller;

import com.example.limit.annotation.RequestQps;
import com.example.limit.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/18 - 20:27
 */
@RestController
public class LimitTestController {

    @GetMapping("/hello")
    public String testHello() {
        return "hello";
    }

    /** 测试限流 */
    @RequestQps(requestCap = 1000, time = 1)
    @GetMapping("/limit")
    public R testLimit() {
        return R.success("访问正常！");
    }

    /** 测试限流 */
    @RequestQps(requestCap = 3, time = 1)
    @GetMapping("/limit2")
    public R testLimit2() {
        return R.success("访问正常！");
    }

    /** 测试限流 */
    @RequestQps(requestCap = 5, time = 10, timeUnit = TimeUnit.SECONDS)
    @GetMapping("/limit3")
    public R testLimit3() {
        return R.success("访问正常！");
    }

}
