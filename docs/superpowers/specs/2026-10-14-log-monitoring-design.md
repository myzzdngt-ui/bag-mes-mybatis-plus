# 箱包 MES 日志及监控设计

## 目标

在小组已有箱包 MES MyBatis-Plus 逆向工程项目上，补充业务操作日志和接口耗时监控能力，使用 AOP 自动采集工单分页接口的请求、响应、异常和耗时数据，并形成可提交的数据库设计文档、运行截图和 Git 提交记录。

## 现状和范围

当前项目使用 Spring Boot 3.5.5、Java 21、MyBatis-Plus 3.5.12 和 MySQL 8.0，已包含 12 张业务核心表及其逆向生成的 Entity、Mapper、Service、Controller 和 XML。工单分页接口为 `GET /work-orders/page`。

本次改动只新增日志监控能力，不改变已有业务表字段，不把日志写入强耦合到工单、报工或质量业务事务中。日志采集以 HTTP Controller 方法为入口，首先覆盖工单分页接口，后续业务接口可以通过增加注解接入。

## 方案选择

### 方案 A：操作日志和接口监控分表，AOP 统一采集

新增 `operation_log` 和 `api_monitor_log` 两张表，由 MyBatis-Plus 生成完整数据访问层，使用一个环绕切面同时记录业务操作和接口耗时。优点是职责清晰、查询索引可独立优化、能分别满足审计和性能分析；缺点是每次请求需要写两条日志。

### 方案 B：单日志表存储全部字段

使用一张宽表同时记录操作和监控字段。实现简单，但业务审计和性能分析字段混杂，索引与数据保留策略难以分别设计。

### 方案 C：接入外部 APM 平台

将数据写入外部监控平台。可观测能力更强，但需要外部服务、账号和网络配置，不适合本次课程项目的本地提交和数据库截图要求。

本项目采用方案 A。

## 数据库设计

### operation_log 操作日志表

| 字段 | 类型 | 说明 | 索引或约束 |
|---|---|---|---|
| log_id | BIGINT | 日志主键 | 主键，自增 |
| trace_id | CHAR(36) | 请求链路标识 | 普通索引 |
| module_name | VARCHAR(64) | 业务模块，如 WORK_ORDER | 模块索引的一部分 |
| operation_type | VARCHAR(32) | 操作类型，如 QUERY、CREATE、UPDATE | 模块索引的一部分 |
| operation_name | VARCHAR(128) | 操作名称 | 便于审计展示 |
| request_method | VARCHAR(16) | HTTP 方法 | 普通字段 |
| request_uri | VARCHAR(255) | 请求 URI | URI 索引的一部分 |
| request_params | JSON | 脱敏后的请求参数 | 业务审计 |
| operator_id | BIGINT | 操作者 ID | 可为空 |
| operator_name | VARCHAR(64) | 操作者名称 | 可为空 |
| success | TINYINT | 是否成功 | 状态索引的一部分 |
| response_code | INT | 业务响应码 | 状态索引的一部分 |
| error_message | VARCHAR(1000) | 异常摘要 | 可为空 |
| client_ip | VARCHAR(64) | 客户端 IP | 可为空 |
| user_agent | VARCHAR(512) | 客户端信息 | 可为空 |
| created_at | DATETIME | 记录时间 | 时间索引的一部分 |

主要索引为 `(module_name, operation_type, created_at)`、`(success, created_at)` 和 `(trace_id)`。日志表不引用具体业务表外键，通过 `trace_id` 和 URI 保持松耦合。

### api_monitor_log 接口监控表

| 字段 | 类型 | 说明 | 索引或约束 |
|---|---|---|---|
| monitor_id | BIGINT | 监控记录主键 | 主键，自增 |
| trace_id | CHAR(36) | 请求链路标识 | 普通索引 |
| request_method | VARCHAR(16) | HTTP 方法 | URI 组合索引的一部分 |
| request_uri | VARCHAR(255) | 请求 URI | URI 组合索引的一部分 |
| handler_method | VARCHAR(255) | Controller 方法 | 便于定位代码 |
| status_code | INT | HTTP 状态码 | 状态索引的一部分 |
| duration_ms | BIGINT | 请求耗时，毫秒 | 慢请求索引的一部分 |
| slow_flag | TINYINT | 是否慢请求 | 慢请求索引的一部分 |
| exception_class | VARCHAR(255) | 异常类型 | 可为空 |
| exception_message | VARCHAR(1000) | 异常摘要 | 可为空 |
| created_at | DATETIME | 记录时间 | 时间索引的一部分 |

主要索引为 `(request_uri, created_at)`、`(slow_flag, duration_ms)`、`(status_code, created_at)` 和 `(trace_id)`。默认慢请求阈值为 1000 毫秒，可通过 `mes.monitor.slow-threshold-ms` 配置。

## AOP 采集流程

1. Controller 方法使用 `@LogMonitor(module = "WORK_ORDER", operation = "QUERY", name = "工单分页查询")` 标记。
2. 切面生成 UUID `trace_id`，读取 HTTP 方法、URI、客户端 IP 和请求参数。
3. 记录开始时间并执行目标方法。
4. 正常返回时计算耗时，写入一条成功的 `operation_log` 和一条 `api_monitor_log`；HTTP 状态默认为 200。
5. 发生异常时计算耗时，写入失败操作日志和异常监控日志，HTTP 状态记为 500，然后继续抛出异常，保证原有异常处理语义不被吞掉。
6. `duration_ms >= 1000` 时将 `slow_flag` 置为 1。

日志写入失败不能覆盖业务接口的原始返回结果，因此采集方法使用独立的异常保护。请求参数只保存 JSON 摘要；密码、令牌等敏感字段不进入日志。

## 对外查询接口

新增两个分页查询接口，供课程演示和日志数据截图使用：

- `GET /operation-logs/page`：按模块、操作类型、URI、成功状态和时间范围查询操作日志。
- `GET /api-monitor-logs/page`：按 URI、HTTP 方法、状态码、慢请求标识和时间范围查询接口监控日志。

两个接口都使用 MyBatis-Plus `PaginationInnerInterceptor`，默认 `pageNum=1`、`pageSize=10`，单页最大 100 条，按创建时间倒序排列。

## 验收标准

- 数据库可成功创建两张日志监控表，并且两张表均有可展示的日志数据。
- 调用工单分页接口后，操作日志和接口监控日志各新增一条记录。
- 能查询到 URI、状态码、耗时、慢请求标识和时间等监控字段。
- 至少验证一次成功请求和一次异常请求，异常请求仍有监控记录。
- Maven 测试通过，项目可启动。
- 提交 Word 设计文档、日志表数据截图、Git 提交记录截图和运行步骤说明。

## 交付物

- 数据库 DDL：`src/main/resources/db/bag_mes_schema.sql` 和日志演示数据脚本。
- Java 代码：注解、AOP 切面、日志查询 DTO、日志查询 Controller 及逆向生成的数据访问层。
- Word 设计文档：日志与监控设计、ER 图、字段说明、调用流程、测试结果和运行说明。
- 截图：日志表数据、项目目录结构、运行结果和 Git 提交记录。
