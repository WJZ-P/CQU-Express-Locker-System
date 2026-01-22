package com.cqu.locker.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 统一返回结果封装类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {
    /**
     * 状态码：200表示成功，其他表示失败
     */
    private Integer code; 
    
    /**
     * 提示信息
     */
    @JsonProperty("message")
    private String msg;   
    
    /**
     * 返回数据
     */
    private T data;       

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.code = 500;
        r.msg = msg;
        return r;
    }
    
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }
}
