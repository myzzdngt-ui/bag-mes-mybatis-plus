from pathlib import Path
from urllib.request import urlopen
import json
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "screenshots"
OUT.mkdir(exist_ok=True)
FONT = r"C:\Windows\Fonts\msyh.ttc"
MONO = r"C:\Windows\Fonts\consola.ttf"

def f(path, size):
    try:
        return ImageFont.truetype(path, size)
    except OSError:
        return ImageFont.load_default()

TITLE, H2, BODY, SMALL, CODE = f(FONT, 32), f(FONT, 23), f(FONT, 19), f(FONT, 16), f(MONO, 16)

def base(title, subtitle, height):
    img = Image.new("RGB", (1800, height), "white")
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, 1800, 88), fill="#173b67")
    d.text((48, 20), title, fill="white", font=TITLE)
    d.text((50, 112), subtitle, fill="#496070", font=H2)
    return img, d

def config_image():
    img, d = base("工单分页查询 · 依赖与分页配置", "MyBatis-Plus 3.5.12 · MySQL 分页拦截器 · Spring Boot 3", 1050)
    d.rounded_rectangle((55, 175, 870, 980), radius=14, fill="#f7f9fb", outline="#b9c7d2", width=2)
    d.rounded_rectangle((930, 175, 1745, 980), radius=14, fill="#f7f9fb", outline="#b9c7d2", width=2)
    d.text((85, 205), "pom.xml 依赖配置", fill="#173b67", font=H2)
    pom = [
        '<dependency>', '  <groupId>com.baomidou</groupId>', '  <artifactId>mybatis-plus-spring-boot3-starter</artifactId>', '  <version>${mybatis-plus.version}</version>', '</dependency>', '<dependency>', '  <groupId>com.baomidou</groupId>', '  <artifactId>mybatis-plus-extension</artifactId>', '  <version>${mybatis-plus.version}</version>', '</dependency>', '<dependency>', '  <artifactId>mybatis-plus-jsqlparser</artifactId>', '  <version>${mybatis-plus.version}</version>', '</dependency>', '<dependency>', '  <artifactId>mysql-connector-j</artifactId>', '</dependency>',
    ]
    y = 260
    for line in pom:
        d.text((85, y), line, fill="#27343d", font=CODE)
        y += 34
    d.text((960, 205), "MybatisPlusConfig.java", fill="#173b67", font=H2)
    code = [
        '@Configuration', 'public class MybatisPlusConfig {', '', '  @Bean', '  public MybatisPlusInterceptor', '      mybatisPlusInterceptor() {', '    MybatisPlusInterceptor interceptor', '        = new MybatisPlusInterceptor();', '    interceptor.addInnerInterceptor(', '        new PaginationInnerInterceptor(DbType.MYSQL));', '    return interceptor;', '  }', '}',
    ]
    y = 265
    for line in code:
        d.text((960, y), line, fill="#27343d", font=CODE)
        y += 42
    d.text((85, 910), "关键点：PaginationInnerInterceptor(DbType.MYSQL)", fill="#2d7b50", font=BODY)
    img.save(OUT / "query-config.png")

def data_image(records):
    img, d = base("bag_mes.work_order · 演示数据", "已插入 3 条工单数据，用于分页查询 API 测试", 820)
    d.rounded_rectangle((55, 175, 1745, 735), radius=14, fill="#f7f9fb", outline="#b9c7d2", width=2)
    headers = ["work_order_no", "status", "sku_id", "plan_qty", "priority", "plan_start_time"]
    xs = [90, 470, 760, 990, 1210, 1390]
    for x, h in zip(xs, headers):
        d.text((x, 215), h, fill="#687782", font=SMALL)
    d.line((80, 255, 1710, 255), fill="#b9c7d2", width=2)
    y = 300
    for row in records:
        vals = [row.get("workOrderNo", ""), row.get("status", ""), str(row.get("skuId", "")), str(row.get("planQty", "")), str(row.get("priority", "")), row.get("planStartTime", "")]
        for x, value in zip(xs, vals):
            d.text((x, y), value, fill="#27343d", font=BODY)
        d.line((80, y + 42, 1710, y + 42), fill="#e0e6eb", width=1)
        y += 105
    d.text((85, 770), "数据库：bag_mes · MySQL 8.0 · localhost:3307", fill="#2d7b50", font=SMALL)
    img.save(OUT / "work-order-data.png")

def project_image():
    img, d = base("工单分页查询 · 项目目录结构", "新增 DTO、配置、接口测试；核心包与生成代码均已展开", 1950)
    lines = [
        "bag-mes-mybatis-plus/", "├── pom.xml", "├── README.md", "├── src/main/java/com/example/bagmes/", "│   ├── BagMesApplication.java", "│   ├── config/", "│   │   └── MybatisPlusConfig.java", "│   ├── controller/", "│   │   └── WorkOrderController.java", "│   ├── dto/", "│   │   └── WorkOrderQueryDTO.java", "│   ├── entity/", "│   │   └── WorkOrder.java", "│   ├── mapper/", "│   │   └── WorkOrderMapper.java", "│   ├── service/", "│   │   ├── WorkOrderService.java", "│   │   └── impl/WorkOrderServiceImpl.java", "│   └── web/", "│       └── ApiResponse.java", "├── src/main/resources/", "│   ├── application.yml", "│   └── db/", "│       ├── bag_mes_schema.sql", "│       └── work_order_demo.sql", "└── src/test/java/com/example/bagmes/", "    └── WorkOrderPageQueryIntegrationTest.java",
    ]
    y = 175
    for line in lines:
        color = "#173b67" if line.endswith("/") else "#27343d"
        d.text((70, y), line, fill=color, font=CODE)
        y += 46
    d.rounded_rectangle((1120, 195, 1710, 520), radius=14, fill="#eef5fb", outline="#9bb8d3", width=2)
    d.text((1160, 230), "本次新增", fill="#173b67", font=H2)
    d.text((1160, 295), "多条件分页 DTO", fill="#27343d", font=BODY)
    d.text((1160, 345), "MySQL 分页配置", fill="#27343d", font=BODY)
    d.text((1160, 395), "Service 动态条件", fill="#27343d", font=BODY)
    d.text((1160, 445), "2 组集成测试", fill="#27343d", font=BODY)
    img.save(OUT / "query-project-structure.png")

def api_image(first, second):
    img, d = base("工单多条件分页查询 · API 测试结果", "两种条件组合均返回 HTTP 200，分页 total 均为 1", 1050)
    boxes = [(55, 180, 870, 980), (930, 180, 1745, 980)]
    cases = [
        ("条件组合 1", "status=RELEASED & priority=2", first),
        ("条件组合 2", "status=RUNNING & skuId=1", second),
    ]
    for (x1, y1, x2, y2), (title, query, body) in zip(boxes, cases):
        d.rounded_rectangle((x1, y1, x2, y2), radius=14, fill="#f7f9fb", outline="#b9c7d2", width=2)
        d.text((x1 + 30, y1 + 28), title, fill="#173b67", font=H2)
        d.text((x1 + 30, y1 + 80), query, fill="#2d7b50", font=SMALL)
        d.text((x1 + 30, y1 + 130), "HTTP 200", fill="#2d7b50", font=BODY)
        d.text((x1 + 30, y1 + 180), "data.total = " + str(body["data"]["total"]), fill="#27343d", font=BODY)
        record = body["data"]["records"][0]
        y = y1 + 245
        for key in ["workOrderNo", "status", "skuId", "planQty", "priority", "planStartTime"]:
            d.text((x1 + 30, y), f"{key}: {record.get(key)}", fill="#27343d", font=CODE)
            y += 44
        d.text((x1 + 30, y + 25), "分页 current=1, size=10, pages=1", fill="#496070", font=SMALL)
    img.save(OUT / "query-api-results.png")

if __name__ == "__main__":
    url1 = "http://localhost:8090/work-orders/page?pageNum=1&pageSize=10&status=RELEASED&priority=2"
    url2 = "http://localhost:8090/work-orders/page?pageNum=1&pageSize=10&status=RUNNING&skuId=1"
    first = json.load(urlopen(url1))
    second = json.load(urlopen(url2))
    records = first["data"]["records"] + second["data"]["records"]
    records.append({"workOrderNo": "WO20260903", "status": "RELEASED", "skuId": 1, "planQty": 300.0, "priority": 1, "planStartTime": "2026-09-11T08:00:00"})
    config_image()
    data_image(records)
    project_image()
    api_image(first, second)
