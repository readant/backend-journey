package com.readant.cms.dto;

import lombok.Data;

/**
 * 登录返回体
 */
@Data
public class LoginVO {

    /** 访问令牌，后续请求在 Header 中携带 */
    private String token;

    /** 管理员基本信息 */
    private AdminVO admin;
}
