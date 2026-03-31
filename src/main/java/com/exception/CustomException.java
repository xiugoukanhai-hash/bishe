package com.exception;

/**
 * 自定义业务异常
 * 用于处理业务逻辑中的异常情况
 */
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误消息
     */
    private String msg;

    /**
     * 构造方法 - 仅消息
     * @param msg 错误消息
     */
    public CustomException(String msg) {
        super(msg);
        this.code = 500;
        this.msg = msg;
    }

    /**
     * 构造方法 - 错误码和消息
     * @param code 错误码
     * @param msg 错误消息
     */
    public CustomException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造方法 - 消息和原因
     * @param msg 错误消息
     * @param cause 原始异常
     */
    public CustomException(String msg, Throwable cause) {
        super(msg, cause);
        this.code = 500;
        this.msg = msg;
    }

    /**
     * 构造方法 - 完整参数
     * @param code 错误码
     * @param msg 错误消息
     * @param cause 原始异常
     */
    public CustomException(int code, String msg, Throwable cause) {
        super(msg, cause);
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

    @Override
    public String toString() {
        return "CustomException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    /**
     * 常用业务异常快捷创建方法
     */
    public static CustomException notFound(String message) {
        return new CustomException(404, message);
    }

    public static CustomException unauthorized(String message) {
        return new CustomException(401, message);
    }

    public static CustomException forbidden(String message) {
        return new CustomException(403, message);
    }

    public static CustomException badRequest(String message) {
        return new CustomException(400, message);
    }

    public static CustomException serverError(String message) {
        return new CustomException(500, message);
    }
}
