package cn.example.tenant.demo.usr.service.impl;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.usr.controller.login.dto.LoginRO;
import cn.example.tenant.demo.usr.entity.SysUser;
import cn.example.tenant.demo.usr.manager.TenantSessionManager;
import cn.example.tenant.demo.usr.manager.UserSessionInfo;
import cn.example.tenant.demo.usr.mapper.SysUserMapper;
import cn.example.tenant.demo.usr.service.AuthException;
import cn.example.tenant.demo.usr.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务：查 {@code sys_user}、BCrypt 校验密码、Redis 会话。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final int STATUS_NORMAL = 1;

    private final SysUserMapper sysUserMapper;
    private final TenantSessionManager sessionManager;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginRO login(String username, String password) {
        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode == null) {
            throw new AuthException("租户标识缺失，请通过租户路径访问");
        }

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getSysTenantCode, tenantCode)
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));

        if (user == null || user.getStatus() == null || user.getStatus() != STATUS_NORMAL) {
            throw new AuthException("用户名或密码错误");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthException("用户名或密码错误");
        }

        UserSessionInfo session = sessionManager.createSession(
                tenantCode, user.getId(), user.getUsername(), user.getNickname(), user.getRole());

        LoginRO ro = new LoginRO();
        ro.setSessionId(session.getSessionId());
        ro.setUsername(session.getUsername());
        ro.setNickname(session.getNickname());
        ro.setRole(session.getRole());
        ro.setExpiresAt(session.getExpiresAt());
        return ro;
    }

    @Override
    public void logout(String sessionId) {
        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode != null && sessionId != null) {
            sessionManager.deleteSession(tenantCode, sessionId);
        }
    }

    @Override
    public UserSessionInfo validateSession(String sessionId) {
        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode == null || sessionId == null) {
            return null;
        }
        return sessionManager.validateSession(tenantCode, sessionId);
    }
}
