from pathlib import Path
from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.shared import Inches, Pt, RGBColor
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
DOC_PATH = ROOT / "docs" / "箱包MES日志及监控设计文档.docx"
SHOT = ROOT / "screenshots"
DOC_PATH.parent.mkdir(exist_ok=True)

def set_cell_shading(cell, fill="FFFFFF"):
    tcPr = cell._tc.get_or_add_tcPr()
    shd = tcPr.find(qn("w:shd"))
    if shd is None:
        shd = OxmlElement("w:shd")
        tcPr.append(shd)
    shd.set(qn("w:fill"), fill)

def set_cell_text(cell, text, bold=False, color="000000"):
    cell.text = ""
    p = cell.paragraphs[0]
    run = p.add_run(str(text))
    run.bold = bold
    run.font.color.rgb = RGBColor.from_string(color)
    run.font.size = Pt(9)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER
    set_cell_shading(cell, "FFFFFF")

def table(doc, headers, rows, widths=None):
    t = doc.add_table(rows=1, cols=len(headers))
    t.alignment = WD_TABLE_ALIGNMENT.CENTER
    t.style = "Table Grid"
    for c, h in zip(t.rows[0].cells, headers):
        set_cell_text(c, h, True)
    for row in rows:
        cells = t.add_row().cells
        for c, v in zip(cells, row):
            set_cell_text(c, v)
    return t

def add_picture(doc, name, width=6.4):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(str(SHOT / name), width=Inches(width))

def make_erd():
    path = SHOT / "log-er-diagram.png"
    img = Image.new("RGB", (1500, 850), "white")
    d = ImageDraw.Draw(img)
    try:
        f1 = ImageFont.truetype(r"C:\Windows\Fonts\msyhbd.ttc", 28)
        f2 = ImageFont.truetype(r"C:\Windows\Fonts\msyh.ttc", 22)
    except Exception:
        f1 = f2 = ImageFont.load_default()
    boxes = {
        "work_order": (90, 120, 470, 330),
        "operation_log": (610, 90, 1010, 340),
        "api_monitor_log": (1080, 90, 1450, 340),
        "product_sku": (90, 520, 470, 730),
        "production_report": (610, 520, 1010, 730),
    }
    fields = {
        "work_order": ["work_order_id PK", "work_order_no", "sku_id", "status", "priority"],
        "operation_log": ["log_id PK", "trace_id", "module_name", "operation_type", "success"],
        "api_monitor_log": ["monitor_id PK", "trace_id", "status_code", "duration_ms", "slow_flag"],
        "product_sku": ["sku_id PK", "sku_code", "style_code", "status"],
        "production_report": ["report_id PK", "work_order_id", "report_time", "good_qty"],
    }
    for name, (x1, y1, x2, y2) in boxes.items():
        d.rounded_rectangle((x1, y1, x2, y2), 16, outline="#1f4e79", width=3, fill="#f8fbff")
        d.text((x1 + 18, y1 + 15), name, fill="#000000", font=f1)
        for i, item in enumerate(fields[name]):
            d.text((x1 + 20, y1 + 65 + i * 28), item, fill="#1f2937", font=f2)
    def arrow(a, b):
        d.line((a, b), fill="#374151", width=4)
        d.polygon([(b[0], b[1]), (b[0]-12, b[1]-8), (b[0]-12, b[1]+8)], fill="#374151")
    arrow((470, 220), (610, 220))
    arrow((470, 260), (610, 610))
    arrow((470, 220), (1080, 220))
    arrow((470, 610), (610, 610))
    d.text((55, 35), "箱包MES日志及监控核心实体关系图", fill="#000000", font=f1)
    img.save(path)

def heading(doc, text, level=1):
    p = doc.add_heading(text, level=level)
    for r in p.runs:
        r.font.color.rgb = RGBColor(0, 0, 0)
    return p

make_erd()
doc = Document()
sec = doc.sections[0]
sec.top_margin = Inches(0.65)
sec.bottom_margin = Inches(0.65)
sec.left_margin = Inches(0.75)
sec.right_margin = Inches(0.75)

p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p.add_run("箱包MES系统日志及监控设计文档")
r.bold = True
r.font.size = Pt(22)
r.font.color.rgb = RGBColor(0, 0, 0)
p = doc.add_paragraph()
p.alignment = WD_ALIGN_PARAGRAPH.CENTER
r = p.add_run("小组项目：bag-mes-mybatis-plus    远程仓库：github.com/myzzdngt-ui/bag-mes-mybatis-plus")
r.font.size = Pt(10)
r.font.color.rgb = RGBColor(0, 0, 0)

heading(doc, "1 设计目标", 1)
doc.add_paragraph("本设计面向箱包MES全流程追溯，记录业务操作、接口耗时与异常，支持按trace_id关联一次请求的业务日志和技术监控日志。设计遵循知识点4、5的分层监控、SLA基线、动态阈值思想，保证日志失败不影响业务主流程。")

heading(doc, "2 核心实体及关系说明", 1)
table(doc, ["实体", "用途", "关键字段"], [
    ["work_order", "生产工单主表", "work_order_id、work_order_no、sku_id、status、priority"],
    ["product_sku", "产品SKU主数据", "sku_id、sku_code、style_code"],
    ["production_report", "工序报工事实", "report_id、work_order_id、report_time、good_qty"],
    ["operation_log", "业务操作日志", "log_id、trace_id、module_name、operation_type、success"],
    ["api_monitor_log", "接口性能/异常日志", "monitor_id、trace_id、status_code、duration_ms、slow_flag"],
])
doc.add_paragraph("关系：product_sku 1:N work_order；work_order 1:N production_report；一次被@LogMonitor标注的接口调用同时写入operation_log和api_monitor_log，两表通过trace_id关联。")
add_picture(doc, "log-er-diagram.png")

heading(doc, "3 日志字段与监控规则", 1)
table(doc, ["层次", "记录内容", "实现方式"], [
    ["系统监控日志", "接口耗时、状态码、慢请求、异常", "api_monitor_log；duration_ms、status_code、slow_flag"],
    ["业务操作日志", "模块、操作类型、操作名称、请求参数、结果", "operation_log；@LogMonitor切面自动采集"],
    ["技术辅助字段", "trace_id、client_ip、User-Agent、error_message", "请求链路关联、脱敏、异常摘要"],
])
doc.add_paragraph("知识点4.5字段对应：trace_id作为全链路标识，client_ip记录来源IP，request_uri/handler_method记录目标接口，cost_ms对应duration_ms，result对应success/status_code，error_message保存异常摘要。密码、token、secret、authorization等字段统一掩码为***。")
doc.add_paragraph("SLA规则：默认慢请求阈值1000ms，可由mes.monitor.slow-threshold-ms调整；日常观察P95/P99，告警阈值取SLA的80%，达到SLA记为慢请求，达到150%可升级严重告警。")

heading(doc, "4 核心表DDL", 1)
doc.add_paragraph("以下为任务10新增核心表DDL，完整DDL位于src/main/resources/db/bag_mes_schema.sql。")
ddl1 = """CREATE TABLE operation_log (
  log_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  trace_id VARCHAR(64) NOT NULL, module_name VARCHAR(64) NOT NULL,
  operation_type VARCHAR(32) NOT NULL, operation_name VARCHAR(128) NOT NULL,
  request_method VARCHAR(16), request_uri VARCHAR(255), request_params JSON,
  success TINYINT NOT NULL, response_code INT, error_message VARCHAR(1000),
  client_ip VARCHAR(64), user_agent VARCHAR(512),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (log_id), KEY idx_operation_trace(trace_id),
  KEY idx_operation_created(created_at)
) ENGINE=InnoDB COMMENT='业务操作日志';"""
ddl2 = """CREATE TABLE api_monitor_log (
  monitor_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  trace_id VARCHAR(64) NOT NULL, request_method VARCHAR(16),
  request_uri VARCHAR(255), handler_method VARCHAR(255), status_code INT,
  duration_ms BIGINT NOT NULL, slow_flag TINYINT NOT NULL DEFAULT 0,
  exception_class VARCHAR(255), exception_message VARCHAR(1000),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (monitor_id), KEY idx_monitor_trace(trace_id),
  KEY idx_monitor_created(created_at), KEY idx_monitor_slow(slow_flag,created_at)
) ENGINE=InnoDB COMMENT='接口性能与异常监控日志';"""
for block in (ddl1, ddl2):
    p = doc.add_paragraph()
    r = p.add_run(block)
    r.font.name = "Consolas"
    r.font.size = Pt(8)

heading(doc, "5 AOP实现与查询接口", 1)
table(doc, ["项目", "说明"], [
    ["@LogMonitor", "标注module、operation、name，当前用于工单分页查询"],
    ["LogMonitorAspect", "Around环绕执行，成功/异常均写两张日志表，并原样返回或抛出业务异常"],
    ["LogSanitizer", "递归处理请求参数，敏感键大小写不敏感并掩码"],
    ["GET /operation-logs/page", "按module、operationType、requestUri、success、时间范围分页查询"],
    ["GET /api-monitor-logs/page", "按requestUri、requestMethod、statusCode、slowFlag、时间范围分页查询"],
])
doc.add_paragraph("分页默认pageNum=1、pageSize=10，pageSize最大100；查询结果按created_at倒序、主键倒序。")

heading(doc, "6 数据、目录和提交截图", 1)
add_picture(doc, "log-table-data.png")
add_picture(doc, "log-project-structure.png")
add_picture(doc, "log-api-test-results.png")
add_picture(doc, "git-commit-record.png")

heading(doc, "7 运行步骤", 1)
for step in [
    "执行 src/main/resources/db/bag_mes_schema.sql 创建bag_mes及核心表。",
    "按需执行 src/main/resources/db/log_monitor_demo.sql 插入日志演示数据。",
    "设置MES_DB_PORT、MES_DB_USERNAME、MES_DB_PASSWORD，运行 ./mvnw.cmd spring-boot:run。",
    "使用浏览器、Postman或Apifox访问 /operation-logs/page 和 /api-monitor-logs/page。",
    "运行 ./mvnw.cmd test 验证生成层、切面日志和分页查询。",
]:
    doc.add_paragraph(step, style="List Number")

heading(doc, "8 小结", 1)
doc.add_paragraph("本方案已将知识点4.5中的日志字段、trace关联、脱敏和SLA监控规则落到箱包MES项目代码、DDL、查询接口和演示材料中，适合作为小组任务提交版本。")

doc.save(DOC_PATH)
print(DOC_PATH)
