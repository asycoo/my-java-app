package cn.example.tenant.demo.boot.filter;

import cn.example.tenant.demo.boot.config.TenantAuthProperties;
import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.util.SessionConstants;
import cn.example.tenant.demo.usr.manager.TenantSessionManager;
import cn.example.tenant.demo.usr.manager.UserSessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * 登录鉴权拦截器：校验 Redis Session，将 userId 写入 {@link TenantUserContext}。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TenantSessionManager sessionManager;
    private final TenantAuthProperties authProperties;

    @PostConstruct
    public void init() {
        log.info("认证排除路径: {}", authProperties.getExcludePaths());
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestPath = request.getRequestURI();
        if (isExcluded(requestPath)) {
            return true;
        }

        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode == null) {
            return true;
        }

        String sessionId = SessionConstants.extractSessionId(request);
        if (sessionId == null) {
            sendUnauthorized(response, "SESSION_REQUIRED", "请先登录");
            return false;
        }

        UserSessionInfo sessionInfo = sessionManager.validateSession(tenantCode, sessionId);
        if (sessionInfo == null) {
            sendUnauthorized(response, "SESSION_INVALID", "会话无效或已过期，请重新登录");
            return false;
        }

        TenantUserContext.setUserId(sessionInfo.getUserId());
        TenantUserContext.setSessionId(sessionId);
        TenantUserContext.setRole(sessionInfo.getRole());
        TenantUserContext.setTenantCode(tenantCode);

        log.debug("认证通过: tenantCode={}, userId={}, path={}", tenantCode, sessionInfo.getUserId(), requestPath);
        return true;
    }

    private boolean isExcluded(String requestPath) {
        List<String> excludePaths = authProperties.getExcludePaths();
        if (excludePaths == null) {
            return false;
        }
        for (String excludePath : excludePaths) {
            String trimmed = excludePath.trim();
            if (requestPath.equals(trimmed) || requestPath.startsWith(trimmed + "/")) {
                return true;
            }
        }
        return false;
    }

    private void sendUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"success\":false,\"code\":\"%s\",\"message\":\"%s\"}", code, message));
        log.warn("认证失败: code={}, message={}", code, message);
    }
}
