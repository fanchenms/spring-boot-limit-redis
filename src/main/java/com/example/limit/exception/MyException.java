package com.example.limit.exception;

/**
 * @Author: yzp
 * @Date: 2021/8/25 10:47
 * @Version: 1.0
 */
public class MyException extends RuntimeException {

    private int code;
    private String msg;

    public MyException() {
    }

    public MyException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
