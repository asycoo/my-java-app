package cn.example.tenant.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记接口所需角色，由 {@code RoleAuthInterceptor} 在登录后校验。
 *
 * <p>示例：{@code @RequireRole("ADMIN")} 仅管理员可调用写接口。</p>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /** 允许的角色列表，满足其一即可 */
    String[] value();
}
