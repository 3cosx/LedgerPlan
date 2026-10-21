# user 与 gateway 模块说明

## 1. 模块职责

| 模块 | 端口 | 职责 |
| --- | --- | --- |
| `ledger-plan-user` | 4082 | 邮箱验证码登录、首次登录自动注册、Sa-Token 会话、用户资料、RBAC 角色/权限读取 |
| `ledger-plan-gateway` | 4080 | 统一转发、Sa-Token 登录校验、RBAC 权限校验、CORS、统一错误响应 |
| `ledger-plan-business` | 4083 | 业务服务（当前仅作为网关路由目标，尚未实现） |

登录链路：

```text
浏览器 -> gateway:4080 -> user:4082
                 |            |
                 |            +-- Redis：验证码 / 重发限制 / 错误次数
                 |            +-- MySQL：t_user / t_role / t_permission / 关联表
                 +-- Sa-Token：校验 token，读取 Redis 中的 SaSession
```

gateway 不引入 `ledger-plan-common`，避免把 Servlet/MyBatis 依赖带进 WebFlux 应用；用户登录时把
`roles`、`permissions`、`status` 写入 Sa-Token Session，gateway 通过 `StpInterface` 读取这些列表做鉴权。

### 1.1 服务边界与防腐层

user 模块内部按用例划分，不把 RBAC 表结构直接暴露给认证逻辑：

- `AuthService`：认证用例（发码、登录、登出、当前用户），只依赖应用层端口 `UserAuthorityPort`。
- `UserInfoService`：普通用户资料用例，只操作 `t_user`，不处理角色/权限。
- `acl.RbacAcl`：RBAC 防腐层适配器，实现 `UserAuthorityPort`，负责把
  `t_role`、`t_permission`、`t_user_role` 的查询结果翻译成 `UserAuthority`，
  是唯一直接使用 `RbacMapper`、`RoleMapper`、`UserRoleLinkMapper` 的类。
- `service.auth.UserAuthority`：应用层权限快照，只包含 `roles`、`permissions` 两个编码列表。

依赖方向：`AuthService -> UserAuthorityPort <- RbacAcl -> Mapper`。更换 RBAC 存储或把鉴权迁移到独立服务时，
只需替换 `UserAuthorityPort` 的实现，认证用例不需要改动。

## 2. 数据库

新建数据库执行 [sql/schema.sql](../sql/schema.sql)，创建 `ledger_plan` 库、`t_user`、`t_role`、`t_permission`、`t_user_role`、`t_role_permission`：

```bash
mysql -uroot -p < sql/schema.sql
```

如果是从旧版本升级（`t_user` 还带有 `role` 列），先执行 `sql/schema.sql` 创建 RBAC 表，再执行
[sql/rbac_migration.sql](../sql/rbac_migration.sql) 把旧角色迁移到 `t_user_role` 并删除 `t_user.role`：

```bash
mysql -uroot -p < sql/schema.sql
mysql -uroot -p < sql/rbac_migration.sql
```

本地默认连接：

- 地址：`127.0.0.1:3306/ledger_plan`
- 用户名：`root`
- 密码：`123456`（通过 `MYSQL_PASSWORD` 覆盖）

## 3. 配置

### 3.1 邮箱验证码

`ledger-plan-user/src/main/resources/application.yml` 中：

```yaml
ledger-plan:
  email:
    provider: ${LEDGER_EMAIL_PROVIDER:log}   # log：验证码打印到日志；smtp：真实发信
    from: ${LEDGER_EMAIL_FROM:no-reply@ledgerplan.local}
    code-length: 6
    expire-minutes: 5
    resend-interval-seconds: 60
    max-attempts: 5
```

本地开发默认 `provider=log`，发送验证码后从 user 日志中读取：

```text
【LedgerPlan】邮箱 test@example.com 的登录验证码为 123456，5 分钟内有效（开发模式，未真实发送）
```

接入真实 SMTP 时设置：

```bash
export LEDGER_EMAIL_PROVIDER=smtp
export LEDGER_EMAIL_HOST=smtp.example.com
export LEDGER_EMAIL_PORT=465
export LEDGER_EMAIL_USERNAME=no-reply@example.com
export LEDGER_EMAIL_PASSWORD=******
```

> 项目使用 `spring-context-support` + `angus-mail` 直接构造 `JavaMailSenderImpl`，没有引入
> `spring-boot-starter-mail`，因此 `spring.mail.*` 不生效；SMTP 参数由 `ledger-plan.email.*` 控制。
> 当前 yml 的 `ssl/starttls` 为 `false`，需要 SSL 的邮箱请按部署环境覆盖这两个值。

### 3.2 Redis

验证码、重发限制、错误次数和 Sa-Token Session 都存在 Redis：

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:127.0.0.1}
      port: ${REDIS_PORT:6379}
```

本地 Redis 无密码。若环境有密码，通过外部配置补充 `spring.data.redis.password`；不要在 yml 中写死空密码
`${REDIS_PASSWORD:}`，否则 Sa-Token 的 Redis 客户端会使用空字符串认证并报 `NOAUTH`。

user 模块显式排除了 Redisson 自动配置（common 传递引入），因为 Redisson 的
`DefaultedRedisConnection.pExpire` 路径会与 Sa-Token 1.39 的调用发生递归并导致 `StackOverflowError`。
user 统一使用 Spring Boot 默认的 Lettuce。

### 3.3 Nacos

本地默认关闭，直接使用 `http://localhost:4082` / `http://localhost:4083` 转发：

```yaml
spring.cloud.nacos.discovery.enabled: ${NACOS_DISCOVERY_ENABLED:false}
spring.cloud.nacos.config.enabled: ${NACOS_CONFIG_ENABLED:false}
```

生产启用 Nacos 时，将 gateway 的路由 URI 改为 `lb://`：

```bash
export NACOS_DISCOVERY_ENABLED=true
export NACOS_CONFIG_ENABLED=true
export NACOS_SERVER_ADDR=127.0.0.1:8848
export USER_SERVICE_URI=lb://ledger-plan-user
export BUSINESS_SERVICE_URI=lb://ledger-plan-business
```

同时需要保证 `spring-cloud-starter-loadbalancer` 在 classpath 中（已引入）。

## 4. 构建与启动

本机系统 `JAVA_HOME` 可能指向无效 JDK，构建时显式指定 JDK 21：

```bash
export JAVA_HOME=F:/java-study/jdk/Java/jdk-21
mvn -o clean package -DskipTests
```

启动顺序：

```bash
# 1. Redis（本地二进制）
F:/java-study/redis/Redis-x64-3.2.100/redis-server.exe

# 2. user
java -jar ledger-plan-user/target/ledger-plan-user-0.0.1-SNAPSHOT.jar

# 3. gateway
java -jar ledger-plan-gateway/target/ledger-plan-gateway-0.0.1-SNAPSHOT.jar
```

> `mvn package` 有时会因为增量编译判断源文件“未变化”而跳过重编译。修改代码后请使用
> `mvn clean package`，否则运行的 jar 里可能仍是旧的 class。

## 5. HTTP 接口

统一响应字段 `code` 是整数：成功固定为 `200`；失败使用业务错误码（如 `4001`、`429`、`5001`），同时 HTTP 状态码仍按语义返回 400/401/403/429/500/503。

### 5.1 发送验证码

```http
POST /auth/send-code
Content-Type: application/json

{"email":"test@example.com"}
```

成功：

```json
{"code":200,"success":true,"message":"ok","data":null}
```

同一邮箱 60 秒内重复发送返回 HTTP 429：

```json
{"code":429,"success":false,"message":"验证码发送过于频繁，请稍后再试","data":null}
```

### 5.2 邮箱验证码登录

```http
POST /auth/login
Content-Type: application/json

{"email":"test@example.com","code":"123456","rememberMe":true}
```

首次登录会自动创建用户（昵称取邮箱 `@` 前缀）。成功返回：

```json
{
  "code": 200,
  "success": true,
  "message": "ok",
  "data": {
    "userId": "2112882730125180930",
    "email": "test@example.com",
    "nickname": "test",
    "roles": ["NORMAL"],
    "permissions": ["account:read", "account:write", "ledger:read", "ledger:write", "report:read"],
    "token": "b7166ec7-6e16-4d2f-87f1-cac7bf0cd7da",
    "tokenTimeout": 2592000
  }
}
```

后续请求通过请求头携带 token：

```http
satoken: b7166ec7-6e16-4d2f-87f1-cac7bf0cd7da
```

### 5.3 当前用户与退出

```http
GET  /auth/me          # 需要登录，返回 UserVO（含 roles/permissions）
POST /auth/logout      # 需要登录
GET  /user/profile     # 需要登录，返回 UserVO
PUT  /user/profile     # 需要登录，body: {"nickname":"新昵称","avatar":"https://..."}
```

`UserVO` 的 `roles` 是角色编码列表，`permissions` 是角色对应权限编码的去重列表；每次登录都会按数据库重新计算并写入 SaSession。

### 5.4 角色与权限

| 角色 | 说明 | 默认权限 |
| --- | --- | --- |
| `NORMAL` | 普通用户 | `ledger:read`、`ledger:write`、`account:read`、`account:write`、`report:read` |
| `MEMBER` | 会员 | 普通用户权限 + `report:export`、`budget:manage` |
| `ADMIN` | 管理员 | 全部权限（含 `admin:manage`、`user:manage`） |

首次登录自动注册的用户默认分配 `NORMAL`。角色和权限关系维护在 `t_role_permission`，用户和角色关系维护在 `t_user_role`。

### 5.5 网关鉴权规则

- 放行：`/auth/send-code`、`/auth/login`、`/actuator/health/**`、`/error`
- `OPTIONS` 预检请求直接放行，由 CORS 配置返回响应头
- 除放行路径外，所有请求必须登录，并校验 SaSession 中的用户状态为 `ACTIVE`
- `/admin/**`：需要 `admin:manage` 权限（仅 `ADMIN` 角色拥有）
- `/report/export`：需要 `report:export` 权限（`MEMBER`/`ADMIN`）
- `/report/**`：需要 `report:read` 权限
- `/budget/**`：需要 `budget:manage` 权限（`MEMBER`/`ADMIN`）
- `/ledger/**`、`/account/**`、`/transaction/**`、`/category/**`：需要 `ledger:read` 权限
- `/user/**`：只需要登录（用于当前用户资料）

网关统一错误响应：

```json
{"code":401,"success":false,"message":"未登录或登录已过期","data":null}
{"code":403,"success":false,"message":"没有操作权限","data":null}
{"code":503,"success":false,"message":"下游服务暂不可用","data":null}
{"code":500,"success":false,"message":"系统内部错误","data":null}
```

## 6. 网关路由

```yaml
spring.cloud.gateway.server.webflux.routes:
  - id: ledger-plan-user
    uri: ${USER_SERVICE_URI:http://localhost:4082}
    predicates:
      - Path=/auth/**,/user/**
  - id: ledger-plan-business
    uri: ${BUSINESS_SERVICE_URI:http://localhost:4083}
    predicates:
      - Path=/ledger/**,/account/**,/transaction/**,/category/**,/budget/**,/report/**
```

Spring Cloud Gateway 4.3 的配置前缀是 `spring.cloud.gateway.server.webflux`；旧的
`spring.cloud.gateway.routes` / `spring.cloud.gateway.globalcors` 只是兼容别名。

CORS 默认允许所有来源，方便本地联调；生产通过 `CORS_ALLOWED_ORIGIN_PATTERNS` 收窄：

```bash
export CORS_ALLOWED_ORIGIN_PATTERNS='https://ledger.example.com'
```

## 7. 已验证场景

- 通过 4080 发送验证码、登录、`/auth/me`、`/user/profile` 均成功
- 未携带 token 访问受保护接口返回 401 JSON
- `NORMAL` 用户访问 `/admin/**`、`/report/export` 返回 403 JSON
- `MEMBER` 用户访问 `/report/export`、`/budget/**` 通过鉴权（business 未启动时返回 503）
- `ADMIN` 用户访问 `/admin/**` 通过鉴权（转发到 user 后得到 404，证明网关已放行）
- 登出后原 token 立即失效
- 同一邮箱重复发送验证码返回 429
- 验证码连续错误 5 次后提示 `4002`
- CORS 预检返回 `Access-Control-Allow-Origin` 等响应头
- `UserInfoServiceTest` 6 个单元测试、`DownstreamUnavailableFilterTest` 3 个单元测试通过

## 8. 待办

- `ledger-plan-business` 尚未实现，`/ledger/**` 等路由在 business 未启动时返回 503 JSON
- 未接入真实 SMTP 做过发信验证（默认 log provider 已验证）
- 角色/权限管理接口暂未实现；当前通过 `t_user_role` 维护用户角色
- 生产环境应把 `allowed-origin-patterns` 收窄，并为 Redis/MySQL 配置独立账号
