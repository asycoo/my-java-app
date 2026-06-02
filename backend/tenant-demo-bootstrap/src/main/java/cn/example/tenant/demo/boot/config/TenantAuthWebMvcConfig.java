package cn.example.tenant.demo.boot.config;

import cn.example.tenant.demo.boot.filter.AuthInterceptor;
import cn.example.tenant.demo.boot.filter.RoleAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册认证与角色拦截器，拦截已去掉租户前缀的 {@code /api/**} 路径。
 */
@Configuration
@EnableConfigurationProperties(TenantAuthProperties.class)
@RequiredArgsConstructor
public class TenantAuthWebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final RoleAuthInterceptor roleAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/**");
        registry.addInterceptor(roleAuthInterceptor).addPathPatterns("/api/**");
    }
}
