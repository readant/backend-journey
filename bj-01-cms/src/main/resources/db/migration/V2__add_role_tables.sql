/*
 * V2__add_role_tables.sql
 * =======================
 * 新增角色表和管理员-角色关联表
 *
 * 变更原因：Phase 2.4 角色权限基础
 * 影响范围：新增 role、admin_role 两张表
 * 回滚方案：DROP TABLE IF EXISTS admin_role, role;
 */

-- 角色表：定义系统中的角色类型
CREATE TABLE IF NOT EXISTS role (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色名称（如：超级管理员、编辑）',
    code        VARCHAR(50)  NOT NULL UNIQUE COMMENT '角色编码（如：ROLE_ADMIN、ROLE_EDITOR）',
    description VARCHAR(255) NULL     COMMENT '角色描述',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_code (code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 管理员-角色关联表：多对多关系
CREATE TABLE IF NOT EXISTS admin_role (
    id          BIGINT   AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    admin_id    BIGINT   NOT NULL COMMENT '管理员ID',
    role_id     BIGINT   NOT NULL COMMENT '角色ID',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    UNIQUE INDEX idx_admin_role (admin_id, role_id),
    INDEX idx_admin_id (admin_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员-角色关联表';