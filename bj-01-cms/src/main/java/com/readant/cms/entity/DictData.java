package com.readant.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dict_data")
public class DictData {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String dictType;

    private String dictCode;

    private String dictValue;

    private Integer sortOrder;

    private Integer status;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}