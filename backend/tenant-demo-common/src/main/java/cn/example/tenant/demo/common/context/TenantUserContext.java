package cn.example.tenant.demo.common.context;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户与用户请求上下文（ThreadLocal 持有）。
 *
 * <p><b>职责</b>：在一次 HTTP 请求内，让 Filter、Interceptor、Service 都能读到当前租户/用户，
 * 无需层层传参。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似 {@code AsyncLocalStorage} 或 middleware 里
 * {@code request.headers} 解析后挂到上下文；Prisma 扩展里自动加 {@code where: { tenantId }}。</p>
 * <p><b>请求生命周期</b>：{@code TenantContextFilter} 写入 tenantCode →
 * {@code AuthInterceptor}（阶段2）写入 userId → 请求结束在 Filter 的 finally 中 {@link #clear()}。</p>
 * <p><b>关键语法</b>：{@code ThreadLocal} 每个线程独立一份；{@code final} 类不可被继承；
 * 私有构造防止被 new。</p>
 */
@Slf4j
public final class TenantUserContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> CURRENT_USER_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> CURRENT_OPERATOR_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<Long> CURRENT_ORG_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_SESSION_ID = new InheritableThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_ROLE = new InheritableThreadLocal<>();

    private TenantUserContext() {
    }

    public static void setTenantCode(String tenantCode) {
        log.debug("设置租户上下文: tenantCode={}", tenantCode);
        CURRENT_TENANT.set(tenantCode);
    }

    public static String getTenantCode() {
        return CURRENT_TENANT.get();
    }

    public static void setUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setOperatorId(Long operatorId) {
        CURRENT_OPERATOR_ID.set(operatorId);
    }

    public static Long getOperatorId() {
        return CURRENT_OPERATOR_ID.get();
    }

    public static void setOrgId(Long orgId) {
        CURRENT_ORG_ID.set(orgId);
    }

    public static Long getOrgId() {
        return CURRENT_ORG_ID.get();
    }

    public static void setSessionId(String sessionId) {
        CURRENT_SESSION_ID.set(sessionId);
    }

    public static String getSessionId() {
        return CURRENT_SESSION_ID.get();
    }

    public static void setRole(String role) {
        CURRENT_ROLE.set(role);
    }

    public static String getRole() {
        return CURRENT_ROLE.get();
    }

    /** 请求结束时必须清理，否则线程池复用线程会导致租户串号 */
    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_USER_ID.remove();
        CURRENT_OPERATOR_ID.remove();
        CURRENT_ORG_ID.remove();
        CURRENT_SESSION_ID.remove();
        CURRENT_ROLE.remove();
        log.debug("清除租户用户上下文");
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}
