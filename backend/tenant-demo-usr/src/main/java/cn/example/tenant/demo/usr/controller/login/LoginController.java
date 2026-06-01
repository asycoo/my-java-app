package cn.example.tenant.demo.usr.controller.login;

import cn.example.tenant.demo.common.util.SessionConstants;
import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.entity.Response;
import cn.example.tenant.demo.usr.controller.login.dto.LoginPO;
import cn.example.tenant.demo.usr.controller.login.dto.LoginRO;
import cn.example.tenant.demo.usr.manager.UserSessionInfo;
import cn.example.tenant.demo.usr.service.AuthException;
import cn.example.tenant.demo.usr.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录 / 登出 / 会话校验接口。
 *
 * <p>对外示例：{@code POST /api/tenant-demo/tenant_a/api/usr/login/w/signin}</p>
 */
@Slf4j
@Tag(name = "登录认证")
@RestController
@RequestMapping("/api/usr/login")
@RequiredArgsConstructor
@Validated
public class LoginController {

    private final AuthService authService; // AuthService authService = new AuthServiceImpl(); 

    @Value("${tenant-demo.app.root:/api/tenant-demo}")
    private String appRoot;

    @Operation(summary = "用户登录")
    @PostMapping("/w/signin")
    public Response<LoginRO> signin(@Valid @RequestBody LoginPO request, HttpServletResponse response) { // response 用于设置 Cookie
        String tenantCode = TenantUserContext.getTenantCode();
        if (tenantCode == null) {
            return Response.fail("MISSING_TENANT", "租户标识缺失，请通过租户路径访问");
        }

        log.info("登录请求: tenantCode={}, username={}", tenantCode, request.getUsername());
        try {
            LoginRO loginRO = authService.login(request.getUsername(), request.getPassword());
            setSessionCookie(response, tenantCode, loginRO.getSessionId());
            return Response.ok(loginRO);
        } catch (AuthException e) {
            return Response.fail("AUTH_FAILED", e.getMessage());
        }
    }

    @Operation(summary = "用户登出")
    @PostMapping("/w/signout")
    public Response<Void> signout(HttpServletRequest request, HttpServletResponse response) {
        String sessionId = SessionConstants.extractSessionId(request);
        authService.logout(sessionId);
        clearSessionCookie(response, TenantUserContext.getTenantCode());
        return Response.ok(null);
    }

    @Operation(summary = "校验会话")
    @GetMapping("/r/validate")
    public Response<UserSessionInfo> validate(@RequestParam String sessionId) {
        if (TenantUserContext.getTenantCode() == null) {
            return Response.fail("MISSING_TENANT", "租户标识缺失");
        }
        UserSessionInfo info = authService.validateSession(sessionId);
        if (info == null) {
            return Response.fail("SESSION_INVALID", "会话无效或已过期");
        }
        return Response.ok(info);
    }

    @Operation(summary = "当前会话")
    @GetMapping("/r/current")
    public Response<UserSessionInfo> current(HttpServletRequest request) {
        String tenantCode = TenantUserContext.getTenantCode();
        String sessionId = SessionConstants.extractSessionId(request);
        if (tenantCode == null || sessionId == null) {
            return Response.fail("NOT_LOGGED_IN", "用户未登录");
        }
        UserSessionInfo info = authService.validateSession(sessionId);
        if (info == null) {
            return Response.fail("SESSION_INVALID", "会话无效或已过期");
        }
        return Response.ok(info);
    }

    private void setSessionCookie(HttpServletResponse response, String tenantCode, String sessionId) {
        Cookie cookie = new Cookie(SessionConstants.TENANT_SESSION_COOKIE_NAME, sessionId);
        cookie.setPath(normalizeCookiePath(appRoot + "/" + tenantCode));
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // false 表示可以在 HTTP 下发送
        cookie.setMaxAge(-1); // -1 表示会话结束时删除
        response.addCookie(cookie);
    }

    private void clearSessionCookie(HttpServletResponse response, String tenantCode) {
        if (tenantCode == null) {
            return;
        }
        Cookie cookie = new Cookie(SessionConstants.TENANT_SESSION_COOKIE_NAME, "");
        cookie.setPath(normalizeCookiePath(appRoot + "/" + tenantCode));
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 0 表示立即删除
        response.addCookie(cookie);
    }

    private String normalizeCookiePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        while (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path.replaceAll("/+", "/");
    }
}
