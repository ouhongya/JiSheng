package com.fh.util;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "返回结果")
public class ResultModel<T> {
    @ApiModelProperty("是否成功: 0-成功  1-失败  2-重新登录 ")
    private int result;
    @ApiModelProperty("描述性原因")
    private String message;
    @ApiModelProperty("业务数据")
    private T data;

    private ResultModel(int result, String message, T data) {
        this.result = result;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<T>(0, "SUCCESS", data);
    }


    public static <T> ResultModel<T> success(String message, T data) {
        return new ResultModel<T>(0, message, data);
    }


    public static ResultModel failure() {
        return new ResultModel< >(1, "FAILURE", null);
    }

    public static ResultModel relogin() {
        return new ResultModel<>(2, "RELOGIN", null);
    }

    public static ResultModel relogin(String message) {
        return new ResultModel<>(2, message, null);
    }

    public static ResultModel failure(String message) {
        return new ResultModel<>(1, message, null);
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
