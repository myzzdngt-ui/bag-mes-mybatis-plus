package com.example.bagmes.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.nio.file.Paths;
import java.util.Collections;

/** 根据 bag_mes 数据库表生成 entity、mapper、service、controller 及 XML。 */
public final class CodeGenerator {
    private CodeGenerator() {}

    public static void main(String[] args) {
        String password = System.getProperty("db.password", System.getenv().getOrDefault("MES_DB_PASSWORD", ""));
        String projectPath = Paths.get(System.getProperty("user.dir")).toAbsolutePath().toString();
        String port = System.getProperty("db.port", System.getenv().getOrDefault("MES_DB_PORT", "3307"));
        String url = "jdbc:mysql://localhost:" + port + "/bag_mes?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true";

        FastAutoGenerator.create(url, "root", password)
                .globalConfig(builder -> builder
                        .author("bag-mes 小组")
                        .disableOpenDir()
                        .outputDir(projectPath + "/src/main/java"))
                .packageConfig(builder -> builder
                        .parent("com.example.bagmes")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, projectPath + "/src/main/resources/mapper")))
                .strategyConfig(builder -> builder
                        .addInclude("product_sku", "material_lot", "bom_version", "bom_item", "routing_version",
                                "routing_operation", "work_order", "material_issue", "production_report",
                                "quality_inspection", "packing_box", "trace_link")
                        .entityBuilder().enableLombok().enableTableFieldAnnotation().formatFileName("%s")
                        .mapperBuilder().enableMapperAnnotation().enableBaseResultMap().enableBaseColumnList()
                        .serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImpl")
                        .controllerBuilder().enableRestStyle())
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
