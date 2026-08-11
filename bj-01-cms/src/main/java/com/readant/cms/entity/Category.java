package com.readant.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 栏目实体 —— 支持多级树形结构
 *
 * parent_id = NULL 表示顶级栏目
 * children 字段用于树形查询，非数据库字段
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 栏目名称 */
    private String name;

    /** 父栏目ID（NULL=顶级栏目） */
    private Long parentId;

    /** 排序序号（越小越靠前） */
    private Integer sortOrder;

    /** 状态：0=隐藏，1=显示 */
    private Integer status;

    /** 子栏目列表（树形查询时使用，非数据库字段） */
    @TableField(exist = false)
    private List<Category> children;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}