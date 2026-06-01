package cn.example.tenant.demo.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 带租户与审计字段的实体基类（组织、成员表共用）。
 */
@Data
public abstract class BaseAuditEntity {

    @TableField(fill = FieldFill.INSERT)
    private String sysTenantCode;

    @TableField(fill = FieldFill.INSERT)
    private Long rAddBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime rAddTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long rModifiedBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime rModifiedTime;
}
