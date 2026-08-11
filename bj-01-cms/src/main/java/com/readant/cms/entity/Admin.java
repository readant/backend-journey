package com.readant.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员实体 —— 对应数据库 admin 表
 *
 * 通俗理解：这个类就是 admin 表在 Java 世界里的"照片"。
 * 表的每一列对应类的一个字段，每一行数据对应一个 Admin 对象。
 *
 * @TableName：告诉 MyBatis-Plus 这个类对应哪张表
 * @Data：Lombok 自动生成 getter/setter/toString 等方法
 */
@Data
@TableName("admin")
public class Admin {

    /** 主键ID（自增） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名（唯一） */
    private String username;

    /** 密码（BCrypt 加密存储，不存明文） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 状态：0=禁用，1=启用 */
    private Integer status;

    /** 创建时间（自动填充，插入时自动设置） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（自动填充，插入和更新时自动设置） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}