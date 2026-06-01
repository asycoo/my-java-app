package cn.example.tenant.demo.boot.filter;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.util.SessionConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 租户上下文 Servlet 过滤器。
 *
 * <p><b>职责</b>：
 * <ol>
 *   <li>从 URL 解析 tenantCode，例如 {@code /api/tenant-demo/acme/api/health/r/ping}</li>
 *   <li>写入 {@link TenantUserContext}</li>
 *   <li>重写 requestURI，去掉租户前缀，使 Controller 仍映射 {@code /api/...}</li>
 * </ol>
 * </p>
 * <p><b>与 Next/Prisma 的类比</b>：Next.js {@code middleware} 里解析 pathname 第一段为 tenant，
 * 再 rewrite 到内部 API 路径。</p>
 * <p><b>请求生命周期</b>：所有请求最先经过本 Filter（除静态资源等排除路径）。</p>
 * <p><b>关键语法</b>：{@code OncePerRequestFilter} 保证每个请求只过滤一次；
 * {@code HttpServletRequestWrapper} 装饰器模式重写 URI。</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextFilter extends OncePerRequestFilter {

    /** @deprecated 使用 {@link SessionConstants#TENANT_SESSION_COOKIE_NAME} */
    @Deprecated
    public static final String TENANT_SESSION_COOKIE_NAME = SessionConstants.TENANT_SESSION_COOKIE_NAME;

    @Value("${tenant-demo.app.root:/api/tenant-demo}")
    private String appRoot;

    private Pattern tenantPathPattern;

    /**
     * Bean 创建后根据 {@link #appRoot} 编译租户路径正则，供 {@link #doFilterInternal} 匹配 URL。
     *
     * <p>正则形如 {@code ^/api/tenant-demo/([租户码])(/api...)$}，只执行一次。</p>
     */
    @PostConstruct
    public void init() {
        String escapedAppRoot = appRoot.startsWith("/") ? appRoot : "/" + appRoot;
        if (escapedAppRoot.endsWith("/")) {
            escapedAppRoot = escapedAppRoot.substring(0, escapedAppRoot.length() - 1);
        }
        escapedAppRoot = Pattern.quote(escapedAppRoot);
        String regex = "^" + escapedAppRoot + "/([A-Za-z0-9_-]+)(/api[/]?.*)$";
        this.tenantPathPattern = Pattern.compile(regex);
        log.info("租户路径匹配模式已初始化: {}", regex);
    }

    /**
     * 单次请求的核心过滤逻辑：解析租户 → 写入上下文 → 重写 URI → 放行后续 Filter/Controller。
     *
     * <p>无论是否匹配租户路径，{@code finally} 中都会 {@link TenantUserContext#clear()}，防止线程复用串租户。</p>
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String uri = request.getRequestURI();
            Matcher matcher = tenantPathPattern.matcher(uri);

            if (matcher.matches()) {
                String tenantCode = matcher.group(1).toLowerCase();
                String rewritePath = matcher.group(2);
                TenantUserContext.setTenantCode(tenantCode);

                TenantRewriteRequestWrapper wrapped =
                        new TenantRewriteRequestWrapper(request, tenantCode, rewritePath);
                // 原始: /api/tenant-demo/tenant_a/api/health/r/ping
                // 重写: /api/health/r/ping
                log.debug("租户上下文: tenantCode={}, 原始URI={}, 重写URI={}",
                        tenantCode, uri, rewritePath);
                chain.doFilter(wrapped, response);
            } else {
                chain.doFilter(request, response);
            }
        } finally {
            TenantUserContext.clear();
        }
    }

    /**
     * 判断当前请求是否跳过本 Filter（Swagger、健康检查、静态资源等无需租户解析的路径）。
     *
     * @return {@code true} 表示直接放行，不做租户解析与 URI 重写
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/health")
                || path.endsWith(".html")
                || path.endsWith(".js")
                || path.endsWith(".css")
                || path.endsWith(".ico");
    }

    /**
     * 从请求中提取登录会话 ID，供阶段2 {@code AuthInterceptor} 校验身份。
     *
     * <p>优先级：{@code X-Session-Id} → {@code X-Auth-Token} → Cookie {@link #TENANT_SESSION_COOKIE_NAME}。</p>
     *
     * @return sessionId，未找到时返回 {@code null}
     */
    public static String extractSessionId(HttpServletRequest request) {
        return SessionConstants.extractSessionId(request);
    }

    /**
     * 装饰器：对外隐藏带租户前缀的 URL，对下游只暴露 {@code /api/...} 形式的内部路径。
     */
    public static class TenantRewriteRequestWrapper extends HttpServletRequestWrapper {

        private final String tenantCode;
        private final String rewritePath;

        /**
         * 保存原始请求及解析出的租户码、重写后的内部 API 路径。
         */
        public TenantRewriteRequestWrapper(HttpServletRequest request,
                                           String tenantCode,
                                           String rewritePath) {
            super(request);
            this.tenantCode = tenantCode;
            this.rewritePath = rewritePath;
        }

        /**
         * 返回去掉租户前缀后的 URI，使 Spring MVC 按 {@code /api/...} 路由到 Controller。
         */
        @Override
        public String getRequestURI() {
            return rewritePath;
        }

        /**
         * 与 {@link #getRequestURI()} 保持一致，避免部分容器用 servletPath 做路由匹配时仍看到带租户的路径。
         */
        @Override
        public String getServletPath() {
            return rewritePath;
        }

        /**
         * 返回本请求解析出的租户编码（小写），供需要直接读包装请求的组件使用。
         */
        public String getTenantCode() {
            return tenantCode;
        }
    }
}
