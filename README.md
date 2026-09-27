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

## 提交材料截图

- [项目目录结构截图](screenshots/project-structure.png)
- [数据库表目录截图](screenshots/database-tables.png)
