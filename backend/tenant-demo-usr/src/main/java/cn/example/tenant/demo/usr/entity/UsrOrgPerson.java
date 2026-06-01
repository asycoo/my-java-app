package cn.example.tenant.demo.usr.entity;

import cn.example.tenant.demo.common.entity.BaseAuditEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组织成员实体，对应表 {@code usr_org_person}。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("usr_org_person")
public class UsrOrgPerson extends BaseAuditEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orgId;

    private String realName;

    private String mobile;

    private String email;

    /** 1=正常, 0=停用 */
    private Integer personStatus;
}
