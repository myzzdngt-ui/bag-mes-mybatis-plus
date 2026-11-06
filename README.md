# 箱包 MES MyBatis-Plus 逆向工程项目

本项目根据分组任务 8 数据库设计文档生成，使用 MyBatis-Plus Generator 依据 `bag_mes` 数据库表生成全套基础代码。

## 环境

- Java 21
- Spring Boot 3.5.5
- MySQL 8.0+
- MyBatis-Plus 3.5.12

## 运行步骤

1. 执行 `src/main/resources/db/bag_mes_schema.sql`，创建 `bag_mes` 数据库及 12 张核心表。
2. 本提交的演示数据库使用本机 3307 端口、root 空密码；切换到自己的 MySQL 3306 实例时，设置 `MES_DB_PORT`、`MES_DB_USERNAME` 和 `MES_DB_PASSWORD`。
3. 运行逆向生成器：

```powershell
./mvnw.cmd -DskipTests compile exec:java -Dexec.mainClass=com.example.bagmes.generator.CodeGenerator
```

4. 启动项目：

```powershell
./mvnw.cmd spring-boot:run
```

生成目录包括 `entity`、`mapper`、`service`、`service.impl`、`controller` 和 `resources/mapper`。

## 工单多条件分页查询

先执行 `src/main/resources/db/work_order_demo.sql` 插入 3 条演示工单，再启动项目。分页接口为：

```text
GET http://localhost:8090/work-orders/page?pageNum=1&pageSize=10&status=RELEASED&priority=2
GET http://localhost:8090/work-orders/page?pageNum=1&pageSize=10&status=RUNNING&skuId=1
```

支持的条件包括：工单号模糊查询、状态、SKU、优先级、计划开始时间起止范围；结果按优先级降序、计划开始时间升序排列。分页插件配置位于 `src/main/java/com/example/bagmes/config/MybatisPlusConfig.java`。

## 分页查询提交材料

- [依赖与分页配置截图](screenshots/query-config.png)
- [工单数据截图](screenshots/work-order-data.png)
- [项目目录结构截图](screenshots/query-project-structure.png)
- [两组 API 测试结果截图](screenshots/query-api-results.png)

## 提交材料截图

- [项目目录结构截图](screenshots/project-structure.png)
- [数据库表目录截图](screenshots/database-tables.png)

## 日志及监控设计任务

- 日志设计文档：[箱包MES日志及监控设计文档](docs/箱包MES日志及监控设计文档.docx)
- 测试方案：[箱包MES自动化测试方案](docs/箱包MES自动化测试方案.docx)
- 运行日志：SLF4J 门面 + Spring Boot 默认 Logback 实现；配置位于 `src/main/resources/logback-spring.xml`
- 请求链路：`RequestTraceLoggingFilter` 生成 `traceId`，写入 MDC 和 `X-Trace-Id` 响应头
- 日志表：`operation_log`、`api_monitor_log` 及其分页查询接口保留用于任务10数据库日志记录查询
- 分页接口：GET /operation-logs/page、GET /api-monitor-logs/page
- 演示数据：src/main/resources/db/log_monitor_demo.sql
- 文件日志：`logs/bag-mes.log`，每天滚动、单文件10MB切分、保留30天、上限1GB
- 材料截图：screenshots/log-table-data.png、screenshots/log-project-structure.png、screenshots/log-api-test-results.png、screenshots/git-commit-record.png

服务代码通过 SLF4J 按级别记录业务事件，HTTP 过滤器记录方法、路径、状态和耗时；慢请求阈值默认为1000ms，可通过 `mes.monitor.slow-threshold-ms` 调整。MDC 中的 traceId 随每条日志输出。日志中禁止记录请求正文、凭据和完整查询参数。任务14移除任务10的 `@LogMonitor`/`LogMonitorAspect` 自动数据库采集能力；任务10的数据库日志表与分页查询代码保留，现有数据库记录仍可查询，新运行日志写入控制台和滚动文件。

任务10的数据库日志设计文档已按任务14修订为 SLF4J + Logback 方案。日志表仍作为任务10的数据库查询材料保留；任务14运行日志按级别写入控制台和文件，不再通过 AOP 自动写入数据库。
