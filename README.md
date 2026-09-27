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
