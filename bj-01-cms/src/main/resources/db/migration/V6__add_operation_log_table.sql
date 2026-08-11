/*
 * V6__add_operation_log_table.sql
 * ==============================
 * 新增操作日志表
 *
 * 变更原因：Phase 4.1 操作日志（AOP 切面自动记录）
 * 影响范围：新增 operation_log 表
 * 回滚方案：DROP TABLE IF EXISTS operation_log;
 */

CREATE TABLE IF NOT EXISTS operation_log (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    admin_id    BIGINT       NULL     COMMENT '操作管理员ID',
    module      VARCHAR(50)  NOT NULL COMMENT '操作模块（如：管理员、文章、栏目）',
    action      VARCHAR(50)  NOT NULL COMMENT '操作类型（如：创建、更新、删除、登录）',
    target_id   BIGINT       NULL     COMMENT '操作对象ID',
    detail      VARCHAR(500) NULL     COMMENT '操作详情',
    ip          VARCHAR(50)  NULL     COMMENT '请求IP',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    INDEX idx_admin_id (admin_id),
    INDEX idx_module (module),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';