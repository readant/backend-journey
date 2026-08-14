package com.readant.cms.common;

import lombok.Data;

/**
 * 统一返回体 —— 所有接口的"标准包装箱"
 *
 * @param <T> 数据类型（泛型），比如返回用户信息时 T=UserVO，返回列表时 T=List<ArticleVO>
 */
@Data
public class R<T> {

    /** 状态码：200 成功，其他为失败 */
    private int code;

    /** 提示信息 */
    private String msg;

    /** 返回的数据 */
    private T data;

    // ---------- 构造方法私有化，不允许外部直接 new，必须通过静态方法创建 ----------

    private R() {}

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ---------- 成功系列 ----------

    /**
     * 操作成功（无返回数据）
     */
    public static <T> R<T> success() {
        return new R<>(200, "success", null);
    }

    /**
     * 操作成功（带返回数据）
     */
    public static <T> R<T> success(T data) {
        return new R<>(200, "success", data);
    }

    /**
     * 操作成功（自定义提示 + 数据）
     */
    public static <T> R<T> success(String msg, T data) {
        return new R<>(200, msg, data);
    }

    // ---------- 失败系列 ----------

    /**
     * 操作失败（默认 500）
     */
    public static <T> R<T> error(String msg) {
        return new R<>(500, msg, null);
    }

    /**
     * 操作失败（自定义状态码）
     */
    public static <T> R<T> error(int code, String msg) {
        return new R<>(code, msg, null);
    }
}
