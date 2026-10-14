# 箱包 MES 日志及监控实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在小组已有箱包 MES MyBatis-Plus 项目上实现操作日志与接口耗时监控，并提交可渲染检查的 Word 设计文档、数据库截图、目录截图、运行测试截图和 Git 记录。

**Architecture:** 以 `operation_log` 和 `api_monitor_log` 两张独立日志表保存审计与性能数据；使用 MyBatis-Plus 逆向工程生成两张表的数据访问层；使用 `@LogMonitor` 注解和环绕 AOP 切面采集工单接口的请求、响应、异常和耗时；使用两个分页查询接口展示日志。

**Tech Stack:** Java 21、Spring Boot 3.5.5、MyBatis-Plus 3.5.12、MySQL 8.0、Maven Wrapper、JUnit 5、Spring Boot Test、python-docx、Pillow、项目内置 DOCX 渲染工具。

**Spec:** `docs/superpowers/specs/2026-10-14-log-monitoring-design.md`

## Global Constraints

- 数据库默认连接为 `localhost:3307/bag_mes`，用户名 `root`，密码通过 `MES_DB_PASSWORD` 提供。
- 慢请求阈值默认必须为 1000 毫秒，并支持配置项 `mes.monitor.slow-threshold-ms`。
- 日志采集失败不能覆盖业务接口原始返回结果。
- 请求参数不得记录密码、令牌等敏感字段。
- 分页查询默认 `pageNum=1`、`pageSize=10`，单页最大 100 条，按创建时间倒序。
- 任何新增生产代码都必须先有对应失败测试；MyBatis-Plus 生成代码、DDL 和配置文件按生成物/配置例外处理，但必须由集成测试覆盖。
- 最终 Word 文档必须通过 DOCX 渲染并逐页检查。

## Review Focus

- AOP 正常返回路径：一次工单查询应生成一条操作日志和一条接口监控日志。
- AOP 异常路径：目标方法抛异常后仍生成失败监控记录，并继续抛出原异常。
- 慢请求边界：耗时等于 1000 毫秒时 `slow_flag=1`。
- 敏感参数：请求参数中的 password、token、secret 不应进入日志 JSON。
- 分页边界：页码小于 1、页大小小于 1 或大于 100 时应被规范化。

### Task 1: Add log table schema and reverse generated layers

**Files:**
- Modify: `src/main/resources/db/bag_mes_schema.sql`
- Create: `src/main/resources/db/log_monitor_demo.sql`
- Modify: `src/main/java/com/example/bagmes/generator/CodeGenerator.java`
- Generate: `src/main/java/com/example/bagmes/entity/OperationLog.java`
- Generate: `src/main/java/com/example/bagmes/entity/ApiMonitorLog.java`
- Generate: corresponding Mapper, XML, Service, ServiceImpl and Controller files
- Test: `src/test/java/com/example/bagmes/LogMonitoringIntegrationTest.java`

**Interfaces:**
- Produces `OperationLogMapper`, `ApiMonitorLogMapper`, `OperationLogService` and `ApiMonitorLogService` for later AOP and query tasks.

- [ ] **Step 1: Write failing database integration tests**

  Add tests that query `operation_log` and `api_monitor_log` through the Spring context and assert that both mapped services are available and can return pages.

- [ ] **Step 2: Run the targeted test and verify it fails**

  Run `./mvnw.cmd -Dtest=LogMonitoringIntegrationTest test`.
  Expected: failure because the two log tables and generated mapper/service beans do not yet exist.

- [ ] **Step 3: Add both DDL definitions and demo data**

  Add the exact fields, indexes and comments from the approved design. Add demo rows for one successful operation and one slow/error monitor record.

- [ ] **Step 4: Extend and run the MyBatis-Plus generator**

  Add `operation_log` and `api_monitor_log` to `CodeGenerator`, execute it against the local `bag_mes` database, and verify the generated files have normal Java names and correct entity mappings.

- [ ] **Step 5: Run the targeted test and verify the schema/layers pass**

  Run the same Maven test and confirm the context loads and both table services are usable.

### Task 2: Implement the AOP logging and monitor capture

**Files:**
- Create: `src/main/java/com/example/bagmes/logging/LogMonitor.java`
- Create: `src/main/java/com/example/bagmes/logging/LogMonitorAspect.java`
- Create: `src/main/java/com/example/bagmes/logging/LogSanitizer.java`
- Modify: `src/main/java/com/example/bagmes/controller/WorkOrderController.java`
- Modify: `src/main/resources/application.yml`
- Test: `src/test/java/com/example/bagmes/LogMonitoringIntegrationTest.java`

**Interfaces:**
- `@LogMonitor(module, operation, name)` marks a Controller method.
- `LogMonitorAspect` surrounds marked methods and writes one `OperationLog` plus one `ApiMonitorLog`.
- `LogSanitizer.sanitize(Object)` returns JSON-safe request text with sensitive keys masked.

- [ ] **Step 1: Add failing success-path and exception-path tests**

  Add a test-only controller endpoint annotated with `@LogMonitor` that throws an exception, plus a real request test for `/work-orders/page`. Assert successful requests create `success=1` and `status_code=200`; exception requests create `success=0` and preserve the exception response.

- [ ] **Step 2: Run the tests and verify they fail for missing annotation/aspect behavior**

  Run `./mvnw.cmd -Dtest=LogMonitoringIntegrationTest test`.
  Expected: no corresponding log rows are created.

- [ ] **Step 3: Implement the annotation and sanitizer**

  Define annotation metadata and mask keys matching `password`, `token`, `secret`, `authorization` case-insensitively before persisting request parameters.

- [ ] **Step 4: Implement the around advice**

  Generate a UUID trace ID, read request metadata, measure elapsed milliseconds, persist normal and exceptional outcomes with an independent try/catch around logging, and mark `slow_flag` when duration is at least the configured threshold.

- [ ] **Step 5: Annotate the work order page endpoint and configure the threshold**

  Add `@LogMonitor(module = "WORK_ORDER", operation = "QUERY", name = "工单分页查询")` and `mes.monitor.slow-threshold-ms: 1000`.

- [ ] **Step 6: Run the targeted integration tests and verify all assertions pass**

  Confirm both success and exception rows exist, sensitive request values are masked, and the original exception remains visible to the caller.

### Task 3: Add paginated log query endpoints

**Files:**
- Create: `src/main/java/com/example/bagmes/dto/OperationLogQueryDTO.java`
- Create: `src/main/java/com/example/bagmes/dto/ApiMonitorLogQueryDTO.java`
- Modify: generated log service interfaces and implementations if custom query methods are needed
- Modify: generated log controllers
- Test: `src/test/java/com/example/bagmes/LogMonitoringIntegrationTest.java`

**Interfaces:**
- `GET /operation-logs/page` accepts module, operation type, URI, success and time range.
- `GET /api-monitor-logs/page` accepts URI, method, status code, slow flag and time range.

- [ ] **Step 1: Add failing endpoint assertions**

  Query each endpoint after the success and exception requests; assert HTTP 200, page metadata, URI filtering and descending creation time.

- [ ] **Step 2: Run the tests and verify endpoint assertions fail**

  Run `./mvnw.cmd -Dtest=LogMonitoringIntegrationTest test` and confirm the endpoints are not yet implemented.

- [ ] **Step 3: Implement DTOs and LambdaQueryWrapper pagination**

  Normalize page number and size, append only non-null conditions, cap size at 100, and sort by `created_at DESC`.

- [ ] **Step 4: Run all integration tests and verify the endpoint responses**

  Confirm the two log endpoints return filtered records and preserve the common `ApiResponse` response shape.

### Task 4: Create the Word design document and evidence screenshots

**Files:**
- Create: `docs/箱包MES日志及监控设计文档.docx`
- Create: `tools/create_log_monitoring_doc.py`
- Create: `tools/make_log_monitoring_screenshots.py`
- Create: `screenshots/log-table-data.png`
- Create: `screenshots/log-project-structure.png`
- Create: `screenshots/log-api-test-results.png`
- Create: `screenshots/git-commit-record.png`
- Modify: `README.md`

**Interfaces:**
- The Word document must include the approved design, ER diagram, field tables, AOP sequence, threshold rules, running steps, test results and repository URL.

- [ ] **Step 1: Capture verified evidence**

  Run the application, call the work order endpoint with success and exception inputs, call both log query endpoints, query the two log tables directly, and capture the outputs used by the screenshot builder.

- [ ] **Step 2: Create the ER diagram and Word document**

  Use the bundled workspace Python runtime and `python-docx`, set document titles and headings to black, use readable Chinese fonts, use light-gray table borders, and include code/config excerpts only where they support the explanation.

- [ ] **Step 3: Render the DOCX and inspect every page**

  Run the document skill's `render_docx.py` into a task-local QA directory, inspect every generated `page-N.png`, and revise the document if any table, code block, diagram or heading is clipped or unreadable.

- [ ] **Step 4: Add running steps and evidence links to README**

  Document schema initialization, demo data, application startup, API calls, log query calls, test command and screenshot locations.

### Task 5: Final verification and repository update

**Files:**
- Modify: none beyond files from Tasks 1-4

- [ ] **Step 1: Run the complete Maven test suite**

  Run `./mvnw.cmd test` and require zero failures and zero errors.

- [ ] **Step 2: Verify database and artifact requirements**

  Confirm both log tables exist, both contain records, all screenshot files exist, and the DOCX renders successfully.

- [ ] **Step 3: Review the diff and commit**

  Run `git diff --check`, inspect `git status`, and commit with `feat: add log monitoring and design document`.

- [ ] **Step 4: Push and verify the remote branch**

  Run `git push origin main`, then `git ls-remote --heads origin` and confirm the new commit is present on `main`.

## Plan Self Review

- The approved spec is covered by Tasks 1 through 5: schema, generated layers, AOP, queries, Word document, screenshots and Git evidence all have explicit tasks.
- The AOP interfaces are defined before the query endpoints consume the generated services.
- The plan includes red-green test cycles for database layers, AOP behavior and query endpoints, plus a final full-suite verification.
- The document task includes the mandatory render-and-inspect loop and keeps QA intermediates separate from the deliverable.
- No placeholders or unresolved choices remain; the slow threshold, endpoint names, page limits and output paths are fixed.
