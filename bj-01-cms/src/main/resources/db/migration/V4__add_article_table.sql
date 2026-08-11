/*
 * V4__add_article_table.sql
 * ========================
 * 新增文章表
 *
 * 变更原因：Phase 3.2 文章管理
 * 影响范围：新增 article 表
 * 回滚方案：DROP TABLE IF EXISTS article;
 */

CREATE TABLE IF NOT EXISTS article (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title       VARCHAR(200) NOT NULL COMMENT '文章标题',
    summary     VARCHAR(500) NULL     COMMENT '文章摘要',
    content     LONGTEXT     NULL     COMMENT '文章内容（富文本）',
    category_id BIGINT       NULL     COMMENT '所属栏目ID',
    cover_image VARCHAR(500) NULL     COMMENT '封面图URL',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=已发布',
    author      VARCHAR(50)  NULL     COMMENT '作者',
    view_count  INT          NOT NULL DEFAULT 0 COMMENT '浏览量',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';