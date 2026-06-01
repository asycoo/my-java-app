package cn.example.tenant.demo.usr.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户内登录账号实体，对应表 {@code sys_user}。
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sysTenantCode;

    private String username;

    private String password;

    private String nickname;

    private String role;

    /** 1=正常 */
    private Integer status;

    private LocalDateTime rAddTime;
}
