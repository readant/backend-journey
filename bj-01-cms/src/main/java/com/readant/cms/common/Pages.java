package com.readant.cms.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 分页参数兜底
 *
 * <p>为什么需要它：接口把 pageSize 直接交给数据库时，一个
 * {@code ?pageSize=1000000} 的请求就能让 MySQL 一次性吐出全表，
 * 内存和连接都会被瞬时打满。这类请求不一定来自攻击者——前端算错一个变量就够了。
 *
 * <p>因此统一在入口处收敛：页码不小于 1，每页条数落在 [1, {@value #MAX_PAGE_SIZE}]。
 */
public final class Pages {

    /** 单页最大条数，超过按此值截断 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 未指定或非法时的默认每页条数 */
    public static final int DEFAULT_PAGE_SIZE = 10;

    private Pages() {}

    /**
     * 构造一个被收敛过的分页对象
     *
     * @param pageNum  页码，从 1 开始；小于 1 按 1 处理
     * @param pageSize 每页条数；非法值回落到默认值，超大值截断到上限
     */
    public static <T> Page<T> of(int pageNum, int pageSize) {
        return new Page<>(Math.max(pageNum, 1), clampSize(pageSize));
    }

    /**
     * 收敛每页条数
     */
    public static int clampSize(int pageSize) {
        if (pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
