package com.tidc.handler;

import com.tidc.handler.emun.ReturnCode;
import lombok.Data;

/**
 * 统一格式返回对象
 *
 * @author 宋佳豪
 * @version 1.0
 */
@Data
public class ResultData<T> {

    private int code;
    private String message;
    private T data;
    private long timestamp;

    public ResultData() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> ResultData<T> success() {
        ResultData<T> result = new ResultData<>();
        result.setCode(ReturnCode.SUCCESS.getCode());
        result.setMessage(ReturnCode.SUCCESS.getMessage());
        result.setData(null);
        return result;
    }

    /**
     * 构建成功响应结果对象
     *
     * @param data 数据
     * @return ResultData<T> 成功响应结果对象
     */
    public static <T> ResultData<T> success(T data) {
        ResultData<T> result = new ResultData<>();
        result.setCode(ReturnCode.SUCCESS.getCode());
        result.setMessage(ReturnCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    /**
     * 构建成功响应结果对象
     *
     * @param data    数据
     * @param message 提示信息
     * @return ResultData<T> 成功响应结果对象
     */
    public static <T> ResultData<T> success(T data, String message) {
        ResultData<T> result = new ResultData<>();
        result.setCode(ReturnCode.SUCCESS.getCode());
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    /**
     * 构建成功响应结果对象
     *
     * @param message 提示信息
     * @return ResultData<T> 成功响应结果对象
     */
    public static <T> ResultData<T> success(String message) {
        ResultData<T> result = new ResultData<>();
        result.setCode(ReturnCode.SUCCESS.getCode());
        result.setMessage(message);
        return result;
    }

    /**
     * 构建失败响应结果对象
     *
     * @param code    状态码
     * @param message 提示信息
     * @return ResultData<T> 失败响应结果对象
     */
    public static <T> ResultData<T> fail(int code, String message) {
        ResultData<T> result = new ResultData<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> ResultData<T> fail(T data, int code, String message) {
        ResultData<T> result = new ResultData<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

}
