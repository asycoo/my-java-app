package cn.example.tenant.demo.common.controller.health.vo;

import lombok.Data;

/**
 * 健康检查返回对象（RO = Response Object，出参 DTO）。
 *
 * <p><b>与 Next/Prisma 的类比</b>：类似 TypeScript 里定义的 API 返回类型 interface。</p>
 */
@Data
public class PingRO {

    private String tenantCode;
    private String message;
}
