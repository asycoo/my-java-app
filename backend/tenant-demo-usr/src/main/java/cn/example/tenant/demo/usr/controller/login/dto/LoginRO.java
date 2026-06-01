package cn.example.tenant.demo.usr.controller.login.dto;

import lombok.Data;

/**
 * 登录成功返回（RO = Response Object）。
 */
@Data
public class LoginRO {

    private String sessionId;

    private String username;

    private String nickname;

    private String role;

    private Long expiresAt;
}
