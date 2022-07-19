package com.example.limit.utils;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * @author yzp
 * @version 1.0
 * @date 2022/7/19 - 1:43
 */
@Data
public class R {

    private int code;
    private String msg;
    private Object data;

    public R(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static R result(int code, String msg, Object data) {
        return new R(code, msg, data);
    }

    public static R success(int code, String msg) {
        return result(code, msg, null);
    }

    public static R success(String msg, Object data) {
        return result(HttpStatus.OK.value(), msg, data);
    }

    public static R success(String msg) {
        return success(msg, null);
    }

    public static R success(Object data) {
        return success("success", null);
    }

    public static R error(int code, String msg) {
        return result(code, msg, null);
    }

    public static R error(String msg, Object data) {
        return result(HttpStatus.BAD_REQUEST.value(), msg, data);
    }

    public static R error(String msg) {
        return error(msg, null);
    }

    public static R error(Object data) {
        return error("error", data);
    }

}
