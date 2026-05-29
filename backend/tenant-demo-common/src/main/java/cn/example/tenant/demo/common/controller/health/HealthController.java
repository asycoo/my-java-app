package cn.example.tenant.demo.common.controller.health;

import cn.example.tenant.demo.common.context.TenantUserContext;
import cn.example.tenant.demo.common.entity.Response;
import cn.example.tenant.demo.common.controller.health.vo.PingRO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查 / 租户路由验证接口。
 *
 * <p><b>职责</b>：阶段1用于验证 {@code TenantContextFilter} 是否正确解析租户并重写路径。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似 {@code GET /api/health} 的 Route Handler。</p>
 * <p><b>请求生命周期</b>：Filter →（阶段2 Interceptor）→ 本 Controller。</p>
 * <p><b>关键语法</b>：{@code @RestController} = @Controller + 返回值自动转 JSON；
 * {@code @GetMapping} 映射 HTTP GET。</p>
 */
@Slf4j
@Tag(name = "健康检查")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    /**
     * 租户连通性探测。
     *
     * <p>对外访问示例：{@code GET /api/tenant-demo/tenant_a/api/health/r/ping}</p>
     * <p>Filter 重写后 Controller 看到的路径：{@code /api/health/r/ping}</p>
     *
     * @return 当前解析到的 tenantCode（来自 ThreadLocal，非前端传入）
     */
    @Operation(summary = "租户路由探测")
    @GetMapping("/r/ping")
    public Response<PingRO> ping() {
        String tenantCode = TenantUserContext.getTenantCode();
        log.info("health ping, tenantCode={}", tenantCode);

        PingRO ro = new PingRO();
        ro.setTenantCode(tenantCode);
        ro.setMessage(tenantCode != null
                ? "租户上下文已设置: " + tenantCode
                : "未匹配租户路径，tenantCode 为空");
        return Response.ok(ro);
    }
}
