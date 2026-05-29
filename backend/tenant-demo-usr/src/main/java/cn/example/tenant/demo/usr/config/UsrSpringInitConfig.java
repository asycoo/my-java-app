package cn.example.tenant.demo.usr.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * usr 模块 Spring 装配配置（阶段1仅占位，阶段2起注册登录/组织/成员 Bean）。
 *
 * <p><b>职责</b>：扫描 usr 包下的 Controller、Service、Manager。</p>
 * <p><b>与 Next/Prisma 的类比</b>：按功能域拆分模块，类似 {@code app/api/usr/*} 路由分组。</p>
 */
@Configuration
@ComponentScan(basePackages = {
        "cn.example.tenant.demo.usr.config",
        "cn.example.tenant.demo.usr.controller",
        "cn.example.tenant.demo.usr.manager",
        "cn.example.tenant.demo.usr.service"
})
@MapperScan(basePackages = "cn.example.tenant.demo.usr.mapper")
public class UsrSpringInitConfig {
}
