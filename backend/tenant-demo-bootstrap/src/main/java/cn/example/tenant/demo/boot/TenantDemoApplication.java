package cn.example.tenant.demo.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用入口。
 *
 * <p><b>职责</b>：启动内嵌 Tomcat，加载所有 {@code @Configuration} 与 Bean。</p>
 * <p><b>与 Next/Prisma 的类比</b>：类似 {@code next dev} 启动的开发服务器入口文件。</p>
 * <p><b>关键语法</b>：{@code main} 方法是 JVM 启动时执行的第一个方法；
 * {@code @SpringBootApplication} 组合了自动配置、组件扫描等注解。</p>
 */
@SpringBootApplication
public class TenantDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenantDemoApplication.class, args);
    }
}
