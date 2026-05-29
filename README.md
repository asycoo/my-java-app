# 租户学习项目 (tenant-demo)

基于 [pubfund-zcb-app](file:///Users/zcy/yjn/repos/zcy-pubfund-zcb/pubfund-zcb-app) 架构的简化多租户示例，面向熟悉 Next.js + Prisma 的前端开发者。

## 实施进度

| 阶段 | 状态 | 内容 |
|------|------|------|
| 1 | ✅ 已完成 | Maven 三模块、租户 Filter、健康检查 |
| 2 | 待实施 | 登录 Session + AuthInterceptor |
| 3 | 待实施 | 组织/成员 CRUD |
| 4 | 待实施 | React + Ant Design 前端 |
| 5 | 待实施 | 角色权限 + 文档 |

## 阶段 1：启动与验收

### 编译并运行

```bash
cd backend
mvn -q -pl tenant-demo-bootstrap -am package -DskipTests
cd tenant-demo-bootstrap
mvn spring-boot:run
```

### 验收命令

```bash
# 带租户前缀（应返回 tenantCode=tenant_a）

期望响应示例：

```json
{
  "success": true,
  "result": {
    "tenantCode": "tenant_a",
    "message": "租户上下文已设置: tenant_a"
  }
}
```

### Swagger

http://localhost:8061/swagger-ui.html

## URL 约定

- 对外：`/{app.root}/{tenantCode}/api/...`
- 默认 app.root：`/api/tenant-demo`
- 示例：`GET /api/tenant-demo/tenant_a/api/health/r/ping`

## 目录结构

```
backend/
  tenant-demo-common/    # Response、TenantUserContext、HealthController
  tenant-demo-usr/       # 阶段2起：登录、组织、成员
  tenant-demo-bootstrap/ # 启动、Filter、application.yml
```
