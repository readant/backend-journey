package com.readant.cms.common;

/**
 * 业务异常 —— 当业务规则被违反时抛出
 *
 * 比如：用户不存在、密码错误、文章已删除等"可预料的错误"
 * 区别于系统异常（空指针、数据库连不上等"不可预料的错误"）
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(500, message);
    }

    public int getCode() {
        return code;
    }
}