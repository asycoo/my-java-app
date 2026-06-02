# Java 后端速查（面向 Next.js + Prisma 前端）

本文档配合 [租户学习项目](.) 使用，帮助把已熟悉的前端概念映射到本仓库 Java 代码。

## 概念对照表

| Java / 本项目 | Next.js + Prisma 类比 |
|---------------|----------------------|
| `Controller` + `@RestController` | App Router 的 Route Handler / API Route |
| `Service` / `ServiceImpl` | `lib/xxx-service.ts` 业务函数 |
| `Mapper` + MyBatis-Plus | Prisma Client `findMany` / `create` |
| `entity/*DO`、`*` 表实体 | `schema.prisma` 的 `model` |
| `*PO` 入参 | 请求 body 的 Zod/TS 类型 |
| `*RO` 出参 | 响应 JSON 的 TS interface |
| `*Cmd` | Service 专用入参（不暴露给 HTTP） |
| `Response<T>` | 统一 `{ success, code, message, result }` |
| `TenantContextFilter` | `middleware` 解析 tenant、rewrite path |
| `AuthInterceptor` | `middleware` 校验 session |
| `RoleAuthInterceptor` + `@RequireRole` | 路由级 role 校验 |
| `TenantUserContext` | `AsyncLocalStorage` / 请求上下文 |
| `application.yml` | `.env` + 框架配置 |
| `pom.xml` | `package.json` + 依赖版本 |

## Maven 模块

```
tenant-demo-app (父 POM)
├── tenant-demo-common   # Response、上下文、注解、健康检查
├── tenant-demo-usr      # 登录、组织、成员
└── tenant-demo-bootstrap # 启动类、Filter、Interceptor、application.yml
```

启动入口：`TenantDemoApplication` → `@Import` 各模块的 `*SpringInitConfig`。

## 常见注解

| 注解 | 作用 |
|------|------|
| `@RestController` | REST 控制器，返回值转 JSON |
| `@RequestMapping` / `@GetMapping` | 路由路径 |
| `@RequestBody` | JSON 请求体 → Java 对象 |
| `@RequestParam` | Query 参数 |
| `@Valid` | 触发参数校验 |
| `@Service` | 注册为 Spring Bean（业务层） |
| `@Component` | 通用 Bean（Filter、Interceptor） |
| `@Mapper` | MyBatis 数据访问接口 |
| `@RequiredArgsConstructor` | Lombok：为 `final` 字段生成构造器（用于注入） |
| `@ConfigurationProperties` | 绑定 `application.yml` 配置块 |

## 请求链路（带租户 + 登录）

```
HTTP /api/tenant-demo/{tenant}/api/usr/org/r/list
  → TenantContextFilter（解析 tenant，重写为 /api/usr/org/r/list）
  → AuthInterceptor（校验 Cookie Session，写入 TenantUserContext）
  → RoleAuthInterceptor（若方法有 @RequireRole 则校验角色）
  → OrgInfoController
  → OrgInfoService
  → UsrOrgInfoMapper → MySQL
```

## MyBatis-Plus 常用写法

```java
// 按租户查询
mapper.selectList(new LambdaQueryWrapper<UsrOrgInfo>()
    .eq(UsrOrgInfo::getSysTenantCode, tenantCode)
    .eq(UsrOrgInfo::getOrgStatus, 1));

// 插入（sys_tenant_code 由 MyMetaObjectHandler 自动填充）
mapper.insert(org);
```

## 统一响应

```java
return Response.ok(data);
return Response.fail("ERROR_CODE", "提示文案");
```

前端在 `request.ts` 里解析 `success`、`result`。

## 角色权限（阶段 5）

- 表字段：`sys_user.role` = `ADMIN` | `USER`
- 写接口标注：`@RequireRole(UserRole.ADMIN)`
- 普通用户 `user1` / `123456` 只能读列表，创建会返回 HTTP 403

## 本地调试

| 服务 | 地址 |
|------|------|
| 后端 | http://localhost:8061 |
| 前端 | http://localhost:5173 |
| Swagger | http://localhost:8061/swagger-ui.html |

```bash
# 后端
cd backend/tenant-demo-bootstrap && mvn spring-boot:run

# 前端
cd frontend && npm run dev
```

## 改代码后记得

1. **重启** `mvn spring-boot:run`（Java 不会热更新 Controller）
2. 执行 `init.sql` 时用 `--default-character-set=utf8mb4`
3. 终端 `export LANG=en_US.UTF-8` 避免 curl 中文乱码

## 跑单测

```bash
cd backend
mvn test
```
