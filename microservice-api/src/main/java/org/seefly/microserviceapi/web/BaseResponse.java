package org.seefly.microserviceapi.web;

/**
 * @author liujianxin
 * @date 2021/4/25 14:19
 */
public class BaseResponse <T>{
    private int status;
    private boolean success;
    private String msg;
    private T data;
    
    public BaseResponse(){
    
    }
    
    
    public BaseResponse(T data){
        this.data = data;
        this.success = true;
        this.status = 200;
        this.msg = "操作成功!";
    }
    
    public BaseResponse(String msg){
        this.success = false;
        this.status = 500;
        this.msg = msg;
    }
    
    public static <T> BaseResponse<T> fail(String msg){
        return new BaseResponse<>(msg);
    }
    
    public static <T> BaseResponse<T> ok(T data){
        return new BaseResponse<>(data);
    }
    
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMsg() {
        return msg;
    }
    
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}
