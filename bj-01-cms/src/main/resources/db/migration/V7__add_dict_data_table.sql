/*
 * V7__add_dict_data_table.sql
 * ==========================
 * 新增数据字典表
 *
 * 变更原因：Phase 4.3 数据字典
 * 影响范围：新增 dict_data 表
 * 回滚方案：DROP TABLE IF EXISTS dict_data;
 */

CREATE TABLE IF NOT EXISTS dict_data (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    dict_type   VARCHAR(50)  NOT NULL COMMENT '字典类型（如：article_status、yes_no）',
    dict_code   VARCHAR(50)  NOT NULL COMMENT '字典编码（如：draft、published）',
    dict_value  VARCHAR(200) NOT NULL COMMENT '字典值（如：草稿、已发布）',
    sort_order  INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态：0=禁用，1=启用',
    remark      VARCHAR(255) NULL     COMMENT '备注',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_dict_type (dict_type),
    UNIQUE INDEX idx_type_code (dict_type, dict_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';