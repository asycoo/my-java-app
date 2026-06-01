package cn.example.tenant.demo.boot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 认证相关配置，绑定 {@code tenant-demo.auth.*}。
 */
@Data
@ConfigurationProperties(prefix = "tenant-demo.auth")
public class TenantAuthProperties {

    private List<String> excludePaths = new ArrayList<>();
}
