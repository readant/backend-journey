package com.readant.cms.dto;

import lombok.Data;

/**
 * 登录返回体
 */
@Data
public class LoginVO {

    /** 访问令牌，后续请求在 Header 中携带 */
    private String token;

    /** 令牌有效期（秒），前端据此提示"登录已过期"或提前续期 */
    private Long expiresIn;

    /** 管理员基本信息 */
    private AdminVO admin;
}
