package cn.example.tenant.demo.boot.config;

import cn.example.tenant.demo.common.config.CommonSpringInitConfig;
import cn.example.tenant.demo.usr.config.UsrSpringInitConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 启动模块总装配：显式引入各子模块的 Spring 配置。
 *
 * <p><b>职责</b>：参考 pubfund-zcb 的 BootSpringInitConfig，用 {@code @Import} 聚合 common/usr，
 * 避免 Spring 默认只扫描 boot 包导致其他模块 Bean 未注册。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似在根 layout 里 import 各 feature 模块的 provider。</p>
 * <p><b>请求生命周期</b>：应用启动时执行一次。</p>
 */
@Configuration
@Import({CommonSpringInitConfig.class, UsrSpringInitConfig.class})
public class BootSpringInitConfig {
}
