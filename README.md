# 租户学习项目 (tenant-demo)

基于 [pubfund-zcb-app](file:///Users/zcy/yjn/repos/zcy-pubfund-zcb/pubfund-zcb-app) 架构的简化多租户示例，面向熟悉 Next.js + Prisma 的前端开发者。

## 实施进度

| 阶段 | 状态 | 内容 |
|------|------|------|
| 1 | ✅ 已完成 | Maven 三模块、租户 Filter、健康检查 |
| 2 | ✅ 已完成 | 登录 Session + AuthInterceptor |
| 3 | 待实施 | 组织/成员 CRUD |
| 4 | 待实施 | React + Ant Design 前端 |
| 5 | 待实施 | 角色权限 + 文档 |

## 本地启动

### 1. MySQL + Redis

```bash
docker compose up -d
```

首次启动会自动执行 `docs/sql/init.sql`（仅当 MySQL 数据卷为空时）。

若库已存在，可手动执行：

```bash
docker exec -i tenant-demo-mysql mysql -uroot -proot tenant_demo < docs/sql/init.sql
```

### 2. 启动后端

```bash
cd backend
mvn -q -pl tenant-demo-bootstrap -am package -DskipTests
cd tenant-demo-bootstrap
mvn spring-boot:run
```

### Swagger

http://localhost:8061/swagger-ui.html

## URL 约定

- 对外：`/{app.root}/{tenantCode}/api/...`
- 默认 app.root：`/api/tenant-demo`
- 示例：`GET /api/tenant-demo/tenant_a/api/health/r/ping`

## 阶段 2：登录与 Session 验收

种子账号（两租户相同）：

| 租户 | 用户名 | 密码 |
|------|--------|------|
| tenant_a | admin | 123456 |
| tenant_b | admin | 123456 |

```bash
# 1. 登录（保存 Cookie）
curl -s -c /tmp/cookies.txt -X POST \
  'http://localhost:8061/api/tenant-demo/tenant_a/api/usr/login/w/signin' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'

# 2. 校验当前会话
curl -s -b /tmp/cookies.txt \
  'http://localhost:8061/api/tenant-demo/tenant_a/api/usr/login/r/current'

# 3. 未登录访问受保护接口 → HTTP 401
curl -s -w "\nHTTP:%{http_code}\n" \
  'http://localhost:8061/api/tenant-demo/tenant_a/api/usr/org/r/list'

# 4. 带 Cookie 访问组织列表 → 200
curl -s -b /tmp/cookies.txt \
  'http://localhost:8061/api/tenant-demo/tenant_a/api/usr/org/r/list'
```

## 目录结构

```
backend/
  tenant-demo-common/    # Response、TenantUserContext、HealthController
  tenant-demo-usr/       # 登录、组织、成员
  tenant-demo-bootstrap/ # 启动、Filter、Interceptor、application.yml
docs/
  sql/init.sql           # 建表与种子数据
```
