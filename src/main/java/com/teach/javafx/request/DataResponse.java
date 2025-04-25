package com.teach.javafx.request;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DataResponse 前端HTTP请求返回数据对象
 * Integer code 返回代码 0 正确返回 1 错误返回信息
 * Object data 返回数据对象
 * String msg 返回正确错误信息
 */
public class DataResponse {
    private Integer code;
    private Object data;
    private String msg;
    public DataResponse(){

    }
    public DataResponse(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static DataResponse fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, DataResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new DataResponse(1, null, "JSON解析失败");
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
