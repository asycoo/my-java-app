package cn.example.tenant.demo.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * common 模块 Spring 装配配置。
 *
 * <p><b>职责</b>：声明本模块哪些包下的类需要被 Spring 扫描注册为 Bean。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似在 Next 项目里集中 export 一批 server utilities，
 * 由主应用 import 一次即可使用。</p>
 * <p><b>请求生命周期</b>：应用启动时执行，不参与单次请求。</p>
 * <p><b>关键语法</b>：{@code @Configuration} 标记配置类；{@code @ComponentScan} 指定扫描包；
 * {@code @MapperScan} 注册 MyBatis Mapper 接口（阶段3起使用）。</p>
 */
@Configuration
@ComponentScan(basePackages = {
        "cn.example.tenant.demo.common.config",
        "cn.example.tenant.demo.common.controller",
        "cn.example.tenant.demo.common.service"
})
@MapperScan(basePackages = "cn.example.tenant.demo.common.mapper")
public class CommonSpringInitConfig {
}
