package cn.example.tenant.demo.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Session Cookie / Header 提取工具。
 */
public final class SessionConstants {

    public static final String TENANT_SESSION_COOKIE_NAME = "tenant-demo-session-id";

    private SessionConstants() {
    }

    public static String extractSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader("X-Session-Id");
        if (sessionId != null && !sessionId.isEmpty()) {
            return sessionId;
        }
        sessionId = request.getHeader("X-Auth-Token");
        if (sessionId != null && !sessionId.isEmpty()) {
            return sessionId;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (TENANT_SESSION_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
