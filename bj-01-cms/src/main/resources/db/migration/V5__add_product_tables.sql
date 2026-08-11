/*
 * V5__add_product_tables.sql
 * =========================
 * 新增产品分类表和产品表
 *
 * 变更原因：Phase 3.4 产品分类
 * 影响范围：新增 product_category、product 两张表
 * 回滚方案：DROP TABLE IF EXISTS product, product_category;
 */

-- 产品分类表（支持多级树形结构）
CREATE TABLE IF NOT EXISTS product_category (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    parent_id   BIGINT       NULL     COMMENT '父分类ID（NULL=顶级分类）',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=隐藏，1=显示',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品分类表';

-- 产品表
CREATE TABLE IF NOT EXISTS product (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(200) NOT NULL COMMENT '产品名称',
    description TEXT         NULL     COMMENT '产品描述',
    category_id BIGINT       NULL     COMMENT '所属分类ID',
    cover_image VARCHAR(500) NULL     COMMENT '封面图URL',
    price       DECIMAL(10,2) NULL    COMMENT '价格',
    status      TINYINT      NOT NULL DEFAULT 0 COMMENT '状态：0=草稿，1=已发布',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品表';