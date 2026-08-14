package com.readant.cms.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作管理员ID */
    private Long adminId;

    /** 操作模块 */
    private String module;

    /** 操作类型 */
    private String action;

    /** 操作对象ID */
    private Long targetId;

    /** 操作详情 */
    private String detail;

    /** 请求IP */
    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
