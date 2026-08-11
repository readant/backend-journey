/*
 * V3__add_category_table.sql
 * ==========================
 * 新增栏目表（支持多级树形结构）
 *
 * 变更原因：Phase 3.1 栏目管理
 * 影响范围：新增 category 表
 * 回滚方案：DROP TABLE IF EXISTS category;
 */

CREATE TABLE IF NOT EXISTS category (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL COMMENT '栏目名称',
    parent_id   BIGINT       NULL     COMMENT '父栏目ID（NULL=顶级栏目）',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序序号（越小越靠前）',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=隐藏，1=显示',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='栏目表';