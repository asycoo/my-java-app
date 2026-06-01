package cn.example.tenant.demo.usr.entity;

import cn.example.tenant.demo.common.entity.BaseAuditEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织实体，对应表 {@code usr_org_info}。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("usr_org_info")
public class UsrOrgInfo extends BaseAuditEntity {

    @TableId(type = IdType.AUTO) // @TableId：自动递增的主键 type=IdType.AUTO 主键类型为自动递增
    private Long id;

    private String orgName;

    private String orgShortName;

    private String orgRemark;

    /** 1=正常, 0=停用 */
    private Integer orgStatus;
}
