package com.readant.cms.dto;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 管理员返回体（脱敏，不返回密码）
 */
@Data
public class AdminVO {

    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private LocalDateTime createdAt;
}
