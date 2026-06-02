package cn.example.tenant.demo.boot.filter;

import cn.example.tenant.demo.common.annotation.RequireRole;
import cn.example.tenant.demo.common.context.TenantUserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

/**
 * 角色鉴权拦截器：在 {@link AuthInterceptor} 之后，根据 {@link RequireRole} 校验当前用户角色。
 */
@Slf4j
@Component
public class RoleAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole requireRole = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequireRole.class);
        if (requireRole == null) {
            requireRole = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequireRole.class);
        }
        if (requireRole == null) {
            return true;
        }

        String currentRole = TenantUserContext.getRole();
        boolean allowed = currentRole != null
                && Arrays.stream(requireRole.value()).anyMatch(r -> r.equalsIgnoreCase(currentRole));

        if (!allowed) {
            sendForbidden(response, "AUTH_FORBIDDEN", "当前角色无权限执行此操作");
            log.warn("角色鉴权失败: role={}, required={}, path={}",
                    currentRole, Arrays.toString(requireRole.value()), request.getRequestURI());
            return false;
        }
        return true;
    }

    private void sendForbidden(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(String.format(
                "{\"success\":false,\"code\":\"%s\",\"message\":\"%s\"}", code, message));
    }
}
