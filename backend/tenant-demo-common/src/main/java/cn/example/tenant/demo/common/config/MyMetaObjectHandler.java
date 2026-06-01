package cn.example.tenant.demo.common.config;

import cn.example.tenant.demo.common.context.TenantUserContext;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充：写入时补全 {@code sys_tenant_code}、操作人、时间戳。
 *
 * <p><b>与 Prisma 的类比</b>：类似 Prisma middleware 在 create/update 时自动加 {@code tenantId}。</p>
 */
// strictInsertFill：字段已有值时 不会覆盖（避免误改）
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        strictInsertFill(metaObject, "sysTenantCode", String.class, TenantUserContext.getTenantCode());
        Long operatorId = resolveOperatorId();
        strictInsertFill(metaObject, "rAddBy", Long.class, operatorId);
        strictInsertFill(metaObject, "rModifiedBy", Long.class, operatorId);
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "rAddTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "rModifiedTime", LocalDateTime.class, now);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "rModifiedBy", Long.class, resolveOperatorId());
        strictUpdateFill(metaObject, "rModifiedTime", LocalDateTime.class, LocalDateTime.now());
    }

    private Long resolveOperatorId() {
        Long operatorId = TenantUserContext.getOperatorId();
        if (operatorId != null) {
            return operatorId;
        }
        Long userId = TenantUserContext.getUserId();
        return userId != null ? userId : 0L;
    }
}
