package cn.example.tenant.demo.boot.filter;

import cn.example.tenant.demo.common.context.TenantUserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 租户 Filter 单测：验证 URI 重写与上下文写入。
 */
class TenantContextFilterTest {

    private final TenantContextFilter filter = new TenantContextFilter();

    @AfterEach
    void tearDown() {
        TenantUserContext.clear();
    }

    @Test
    @DisplayName("匹配租户路径时应重写 URI 并设置 tenantCode")
    void shouldRewriteUriAndSetTenant() throws Exception {
        ReflectionTestUtils.setField(filter, "appRoot", "/api/tenant-demo");
        filter.init();

        MockHttpServletRequest request = new MockHttpServletRequest(
                "GET", "/api/tenant-demo/tenant_a/api/health/r/ping");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> {
            assertThat(TenantUserContext.getTenantCode()).isEqualTo("tenant_a");
            assertThat(((HttpServletRequest) req).getRequestURI()).isEqualTo("/api/health/r/ping");
        };

        filter.doFilter(request, response, chain);

        assertThat(TenantUserContext.getTenantCode()).isNull();
    }
}
