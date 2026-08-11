/*
 * V1__init_schema.sql
 * ===================
 * 初始化建表：管理员表
 *
 * 变更原因：Phase 2 管理员与权限模块的第一个迁移脚本
 * 影响范围：新增 admin 表
 * 回滚方案：DROP TABLE IF EXISTS admin;
 */

-- 管理员表：存储后台管理系统的登录用户
CREATE TABLE IF NOT EXISTS admin (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '登录用户名（唯一）',
    password    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt 加密存储）',
    real_name   VARCHAR(50)  NULL     COMMENT '真实姓名',
    email       VARCHAR(100) NULL     COMMENT '邮箱',
    phone       VARCHAR(20)  NULL     COMMENT '手机号',
    avatar      VARCHAR(500) NULL     COMMENT '头像URL',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';