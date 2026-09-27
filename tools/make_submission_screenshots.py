from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "screenshots"
OUT.mkdir(exist_ok=True)

FONT_PATH = r"C:\Windows\Fonts\msyh.ttc"
MONO_PATH = r"C:\Windows\Fonts\consola.ttf"

def font(path, size):
    try:
        return ImageFont.truetype(path, size)
    except OSError:
        return ImageFont.load_default()

TITLE = font(FONT_PATH, 34)
SUBTITLE = font(FONT_PATH, 21)
TEXT = font(FONT_PATH, 20)
SMALL = font(FONT_PATH, 17)
MONO = font(MONO_PATH, 17)

def canvas(title, subtitle, height):
    img = Image.new("RGB", (1800, height), "white")
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, 1800, 96), fill="#173b67")
    d.text((54, 22), title, fill="white", font=TITLE)
    d.text((56, 119), subtitle, fill="#496070", font=SUBTITLE)
    return img, d

def project_tree():
    img, d = canvas("箱包 MES · MyBatis-Plus 逆向工程项目结构", "所有核心包已展开，展示源码、配置、DDL 与生成器文件", 3000)
    lines = [
        (0, "bag-mes-mybatis-plus/", True),
        (1, "├── pom.xml", False),
        (1, "├── README.md", False),
        (1, "├── .gitignore", False),
        (1, "├── src/main/java/com/example/bagmes/", True),
        (2, "├── BagMesApplication.java", False),
        (2, "├── controller/", True),
    ]
    controllers = ["BomItemController.java", "BomVersionController.java", "MaterialIssueController.java", "MaterialLotController.java", "PackingBoxController.java", "ProductionReportController.java", "ProductSkuController.java", "QualityInspectionController.java", "RoutingOperationController.java", "RoutingVersionController.java", "TraceLinkController.java", "WorkOrderController.java"]
    lines += [(3, "├── " + x, False) for x in controllers]
    lines += [(2, "├── entity/", True)]
    entities = ["BomItem.java", "BomVersion.java", "MaterialIssue.java", "MaterialLot.java", "PackingBox.java", "ProductionReport.java", "ProductSku.java", "QualityInspection.java", "RoutingOperation.java", "RoutingVersion.java", "TraceLink.java", "WorkOrder.java"]
    lines += [(3, "├── " + x, False) for x in entities]
    lines += [(2, "├── generator/", True), (3, "└── CodeGenerator.java", False), (2, "├── mapper/", True)]
    mappers = [x.replace("Controller", "Mapper") for x in controllers]
    lines += [(3, "├── " + x, False) for x in mappers]
    lines += [(2, "└── service/", True)]
    services = [x.replace("Controller", "Service") for x in controllers]
    lines += [(3, "├── " + x, False) for x in services]
    lines += [(3, "└── impl/", True)]
    lines += [(4, "    ├── " + x.replace("Service", "ServiceImpl"), False) for x in services]
    lines += [(1, "└── src/main/resources/", True), (2, "├── application.yml", False), (2, "├── db/bag_mes_schema.sql", False), (2, "└── mapper/", True)]
    xmls = [x.replace("Controller.java", "Mapper.xml") for x in controllers]
    lines += [(3, "    ├── " + x, False) for x in xmls]
    y = 180
    for depth, line, is_dir in lines:
        x = 65 + depth * 34
        color = "#173b67" if is_dir else "#27343d"
        d.text((x, y), line, fill=color, font=MONO)
        y += 31
    d.rounded_rectangle((1240, 185, 1735, 500), radius=16, fill="#eef5fb", outline="#9bb8d3", width=2)
    d.text((1270, 215), "逆向工程成果", fill="#173b67", font=SUBTITLE)
    d.text((1270, 270), "12 个实体类", fill="#27343d", font=TEXT)
    d.text((1270, 315), "12 个 Mapper + XML", fill="#27343d", font=TEXT)
    d.text((1270, 360), "12 个 Service / Impl", fill="#27343d", font=TEXT)
    d.text((1270, 405), "12 个 Controller", fill="#27343d", font=TEXT)
    d.text((1270, 450), "Spring Boot 3 + MP 3.5", fill="#27343d", font=SMALL)
    img.save(OUT / "project-structure.png")

def database_tree():
    tables = [
        ("bom_item", 9), ("bom_version", 10), ("material_issue", 12), ("material_lot", 11),
        ("packing_box", 11), ("product_sku", 12), ("production_report", 13), ("quality_inspection", 13),
        ("routing_operation", 10), ("routing_version", 9), ("trace_link", 10), ("work_order", 18),
    ]
    img, d = canvas("bag_mes 数据库表目录", "MySQL 8.0 · DDL 执行结果 · 共 12 张核心表", 950)
    d.rounded_rectangle((60, 180, 1740, 870), radius=12, fill="#f7f9fb", outline="#b9c7d2", width=2)
    d.rectangle((60, 180, 430, 870), fill="#edf2f6")
    d.text((95, 215), "SCHEMAS", fill="#687782", font=SMALL)
    d.text((115, 270), "▾  bag_mes", fill="#173b67", font=TEXT)
    d.text((145, 322), "▾  Tables (12)", fill="#173b67", font=TEXT)
    d.text((145, 370), "▸  Views", fill="#687782", font=TEXT)
    d.text((145, 418), "▸  Procedures", fill="#687782", font=TEXT)
    d.line((430, 180, 430, 870), fill="#b9c7d2", width=2)
    d.text((500, 215), "表名", fill="#687782", font=SMALL)
    d.text((1280, 215), "字段数", fill="#687782", font=SMALL)
    d.line((500, 250, 1690, 250), fill="#b9c7d2", width=2)
    y = 278
    for name, count in tables:
        d.text((515, y), "▦  " + name, fill="#27343d", font=TEXT)
        d.text((1300, y), str(count), fill="#496070", font=TEXT)
        d.line((500, y + 36, 1690, y + 36), fill="#e0e6eb", width=1)
        y += 45
    d.text((500, 826), "状态：DDL 已成功执行 · schema: bag_mes · port: 3307", fill="#2d7b50", font=SMALL)
    img.save(OUT / "database-tables.png")

if __name__ == "__main__":
    project_tree()
    database_tree()
