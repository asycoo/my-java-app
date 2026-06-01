package cn.example.tenant.demo.usr.manager;

import lombok.Data;

/**
 * Redis 中保存的会话摘要，返回给前端或写入 {@code TenantUserContext}。
 */
@Data
public class UserSessionInfo {

    private String sessionId;

    private String tenantCode;

    private Long userId;

    private String username;

    private String nickname;

    private String role;

    /** 过期时间戳（毫秒） */
    private Long expiresAt;
}
