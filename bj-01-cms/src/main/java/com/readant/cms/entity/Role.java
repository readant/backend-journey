package com.readant.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体 —— 对应数据库 role 表
 */
@Data
@TableName("role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色名称（如：超级管理员、编辑） */
    private String name;

    /** 角色编码（如：ROLE_ADMIN、ROLE_EDITOR） */
    private String code;

    /** 角色描述 */
    private String description;

    /** 状态：0=禁用，1=启用 */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}