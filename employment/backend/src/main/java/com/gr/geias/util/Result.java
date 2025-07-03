package com.gr.geias.util;

import lombok.Data;

/**
 * 统一API响应结果封装
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 成功
     * @param data 数据
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 成功
     * @param message 成功消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> ok(String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        return result;
    }

    /**
     * 成功
     * @param data 数据
     * @param message 成功消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> ok(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 失败
     * @param message 失败消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    /**
     * 失败
     * @param code 状态码
     * @param message 失败消息
     * @param <T> 数据类型
     * @return Result
     */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}