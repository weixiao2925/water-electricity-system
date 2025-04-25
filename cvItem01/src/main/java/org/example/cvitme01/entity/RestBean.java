package org.example.cvitme01.entity;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;

//统一返回值
public record RestBean<T>(int code, T data, String message) {
    //----200成功
    public static <T> RestBean<T> success(T data){
        return new RestBean<>(200,data,"请求成功");
    }
    //无参(Get请求)
    public static <T> RestBean<T> success(){
        return success(null);
    }

    //----错误
    public static <T> RestBean<T> failure(int code,String message){
        return new RestBean<>(code,null,message);
    }

    //----401错误(请求没有经过授权,没有通过验证)
    public static <T> RestBean<T> unauthorized(String message){
        return failure(401,message);
    }

    //---403错误('请求被拒绝访问','通过了验证，但是没有权限')
    public static <T> RestBean<T> forbidden(String message){
        return failure(403,message);
    }

    //封装当前对象为json字符串格式（这里就算为null也能存进去null）
    public String asJsonString(){
        return JSONObject.toJSONString(this, JSONWriter.Feature.WriteNulls);
    }
}
