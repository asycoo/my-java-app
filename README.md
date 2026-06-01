# 租户学习项目 (tenant-demo)

基于 [pubfund-zcb-app](file:///Users/zcy/yjn/repos/zcy-pubfund-zcb/pubfund-zcb-app) 架构的简化多租户示例，面向熟悉 Next.js + Prisma 的前端开发者。

## 实施进度

| 阶段 | 状态 | 内容 |
|------|------|------|
| 1 | ✅ 已完成 | Maven 三模块、租户 Filter、健康检查 |
| 2 | ✅ 已完成 | 登录 Session + AuthInterceptor |
| 3 | ✅ 已完成 | 组织/成员 CRUD + MyMetaObjectHandler |
| 4 | 待实施 | React + Ant Design 前端 |
| 5 | 待实施 | 角色权限 + 文档 |

## 本地启动

### 1. MySQL + Redis

```bash
docker compose up -d
```

首次启动会自动执行 `docs/sql/init.sql`（仅当 MySQL 数据卷为空时）。

若库已存在，可手动执行（**必须**带 utf8mb4，否则种子中文会乱码）：

```bash
docker exec -i tenant-demo-mysql mysql -uroot -proot --default-character-set=utf8mb4 tenant_demo < docs/sql/init.sql
```

### 2. 启动后端

```bash
cd backend
mvn -q -pl tenant-demo-bootstrap -am package -DskipTests
cd tenant-demo-bootstrap
mvn spring-boot:run
```

**重要**：修改 Java 代码（例如阶段 3 新增组织接口）后，必须**停止并重新执行** `mvn spring-boot:run`，否则仍是旧进程，会出现 `SYSTEM_ERROR` 或组织列表一直为空。

### 终端中文乱码

**1. 终端编码**

```bash
export LANG=en_US.UTF-8
export LC_ALL=en_US.UTF-8
```

**2. 查看 JSON（推荐，直接显示中文，而不是 `\u65b0`）**

```bash
curl -s -b /tmp/cookies.txt \
  'http://localhost:8061/api/tenant-demo/tenant_a/api/usr/org/r/list' \
  | python3 -c "import sys,json; print(json.dumps(json.load(sys.stdin), ensure_ascii=False, indent=2))"
```

`python3 -m json.tool` 默认会把中文转成 `\uXXXX` 转义，不是接口坏了。

**3. 种子数据乱码（如 `ç¤ºä¾‹`）**

说明导入 `init.sql` 时未用 utf8mb4，请用上一节的 `docker exec ... --default-character-set=utf8mb4` 重新执行脚本。

后端已在 `application.yml` 中强制 HTTP 响应 UTF-8；改配置后需重启 `spring-boot:run`。

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

docker终端：SET NAMES utf8mb4;解决中文问题
curl调接口最后加：| python3 -c "import sys,json; print(json.dumps(json.load(sys.stdin), ensure_ascii=False, indent=2))"

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

# 5. 阶段3：新增组织
curl -s -b /tmp/cookies.txt -X POST \
  'http://localhost:8061/api/tenant-demo/tenant_a/api/usr/org/w/create' \
  -H 'Content-Type: application/json' \
  -d '{"orgName":"新组织","orgShortName":"新"}'

# 6. 租户隔离：tenant_b 看不到 tenant_a 的组织（列表仅本租户）
curl -s -b /tmp/cookies_b.txt -c /tmp/cookies_b.txt -X POST \
  'http://localhost:8061/api/tenant-demo/tenant_b/api/usr/login/w/signin' \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
curl -s -b /tmp/cookies_b.txt \
  'http://localhost:8061/api/tenant-demo/tenant_b/api/usr/org/r/list'
```

## 阶段 3：组织/成员 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/usr/org/r/list?orgName=` | 组织列表（本租户） |
| POST | `/api/usr/org/w/create` | 新增组织 |
| PUT | `/api/usr/org/w/update` | 更新组织 |
| POST | `/api/usr/org/w/disable?orgId=` | 停用组织 |
| GET | `/api/usr/person/r/list?orgId=&pageNum=1&pageSize=10` | 成员分页 |
| POST | `/api/usr/person/w/create` | 新增成员 |
| PUT | `/api/usr/person/w/update` | 更新成员 |
| POST | `/api/usr/person/w/disable?personId=` | 停用成员 |

重新初始化库（含组织表）：

```bash
docker exec -i tenant-demo-mysql mysql -uroot -proot --default-character-set=utf8mb4 tenant_demo < docs/sql/init.sql
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
