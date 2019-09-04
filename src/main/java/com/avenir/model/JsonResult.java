package com.avenir.model;

import com.avenir.constant.ResultCode;
import com.fasterxml.jackson.annotation.JsonManagedReference;

public class JsonResult {
    private Integer code;
    private String msg;
    private Object data;

    public JsonResult(){}

    public JsonResult(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static JsonResult success() {
       JsonResult result = new JsonResult();
       result.setCode(ResultCode.SUCCESS);
       result.setMsg("操作成功");
       return result;
    }

    public static JsonResult success(Object data) {
        return new JsonResult(ResultCode.SUCCESS, "操作成功", data);
    }

    public static JsonResult success(String msg) {
        JsonResult result = new JsonResult();
        result.setCode(ResultCode.SUCCESS);
        result.setMsg(msg);
        return result;
    }

    public static JsonResult error() {
        JsonResult result = new JsonResult();
        result.setCode(ResultCode.FAIL);
        result.setMsg("操作失败");
        return result;
    }

    public static JsonResult error(String msg, Object data) {
        JsonResult result = new JsonResult();
        result.setCode(ResultCode.EXISTSED);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static JsonResult error(Integer code, String msg) {
        JsonResult result = new JsonResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
