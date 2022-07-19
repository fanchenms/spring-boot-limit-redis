package com.example.limit.exception;

import com.example.limit.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 集中处理所有异常
 * @Author: yzp
 * @Date: 2021/8/24 14:48
 * @Version: 1.0
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.example.limit.controller")
public class ExceptionControllerAdvice {

    /**
     * 运行时异常，主动抛出
     * @param e
     * @return
     */
    @ExceptionHandler(MyException.class)
    public R handleRuntimeException(MyException e) {
        log.info("运行时异常：code=[{}] msg=[{}]", e.getCode(),e.getMsg());
        return R.error(e.getCode(), e.getMsg());
    }


    /**
     * 上面捕获不到的异常都交由此处理
     * @param throwable
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("错误：{}", throwable.getMessage());
        throwable.printStackTrace();
        return R.error(HttpStatus.BAD_REQUEST.value(), "error");
    }


}
