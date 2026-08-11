package com.readant.cms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新管理员请求体
 */
@Data
public class AdminUpdateReq {

    @Size(min = 3, max = 50, message = "用户名长度 3-50 个字符")
    private String username;

    @Size(min = 6, max = 100, message = "密码长度 6-100 个字符")
    private String password;

    @Size(max = 50, message = "真实姓名不超过 50 字")
    private String realName;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    private Integer status;
}