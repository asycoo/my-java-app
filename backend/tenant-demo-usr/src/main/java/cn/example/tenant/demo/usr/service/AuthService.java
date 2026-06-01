package cn.example.tenant.demo.usr.service;

import cn.example.tenant.demo.usr.controller.login.dto.LoginRO;
import cn.example.tenant.demo.usr.manager.UserSessionInfo;

/**
 * 登录与会话校验服务。
 */
public interface AuthService {

    LoginRO login(String username, String password);

    void logout(String sessionId);

    UserSessionInfo validateSession(String sessionId);
}
