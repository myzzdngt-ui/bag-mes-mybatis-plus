from pathlib import Path

from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.table import WD_CELL_VERTICAL_ALIGNMENT, WD_TABLE_ALIGNMENT
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Inches, Pt, RGBColor
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
DOCS = ROOT / "docs"
SHOTS = ROOT / "screenshots"
DESIGN = DOCS / "箱包MES日志及监控设计文档.docx"
TESTPLAN = DOCS / "箱包MES自动化测试方案.docx"
ERD = SHOTS / "task14-logging-erd.png"


def set_run_font(run, size=10.5, bold=False, color="000000", name="宋体"):
    run.font.name = name
    run._element.rPr.rFonts.set(qn("w:eastAsia"), name)
    run.font.size = Pt(size)
    run.bold = bold
    run.font.color.rgb = RGBColor.from_string(color)


def add_text(doc, text, style=None):
    p = doc.add_paragraph(style=style)
    r = p.add_run(text)
    set_run_font(r)
    p.paragraph_format.space_after = Pt(5)
    p.paragraph_format.line_spacing = 1.15
    return p


def heading(doc, text, level=1):
    p = doc.add_heading(text, level=level)
    p.paragraph_format.keep_with_next = True
    p.paragraph_format.space_before = Pt(10 if level == 1 else 7)
    p.paragraph_format.space_after = Pt(4)
    for r in p.runs:
        set_run_font(r, 15 if level == 1 else 12, True, "000000", "黑体")
    return p


def shade(cell, fill="FFFFFF"):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = tc_pr.find(qn("w:shd"))
    if shd is None:
        shd = OxmlElement("w:shd")
        tc_pr.append(shd)
    shd.set(qn("w:fill"), fill)


def set_cell(cell, text, bold=False):
    cell.text = ""
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER
    p = cell.paragraphs[0]
    p.paragraph_format.space_after = Pt(1)
    p.paragraph_format.space_before = Pt(1)
    r = p.add_run(str(text))
    set_run_font(r, 8.5, bold, "000000")
    shade(cell, "FFFFFF")


def add_table(doc, headers, rows):
    table = doc.add_table(rows=1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    table.autofit = True
    for cell, value in zip(table.rows[0].cells, headers):
        set_cell(cell, value, True)
    tr_pr = table.rows[0]._tr.get_or_add_trPr()
    repeat = OxmlElement("w:tblHeader")
    repeat.set(qn("w:val"), "true")
    tr_pr.append(repeat)
    for row in rows:
        cells = table.add_row().cells
        for cell, value in zip(cells, row):
            set_cell(cell, value)
    doc.add_paragraph().paragraph_format.space_after = Pt(2)
    return table


def add_bullets(doc, values):
    for value in values:
        add_text(doc, value, "List Bullet")


def add_numbered(doc, values):
    for value in values:
        add_text(doc, value, "List Number")


def setup_doc(title, subtitle):
    doc = Document()
    sec = doc.sections[0]
    sec.page_width = Inches(8.5)
    sec.page_height = Inches(11)
    sec.top_margin = Inches(0.65)
    sec.bottom_margin = Inches(0.65)
    sec.left_margin = Inches(0.75)
    sec.right_margin = Inches(0.75)
    sec.header_distance = Inches(0.3)
    sec.footer_distance = Inches(0.3)

    normal = doc.styles["Normal"]
    normal.font.name = "宋体"
    normal._element.rPr.rFonts.set(qn("w:eastAsia"), "宋体")
    normal.font.size = Pt(10.5)
    normal.font.color.rgb = RGBColor(0, 0, 0)
    for style_name in ("List Bullet", "List Number"):
        style = doc.styles[style_name]
        style.font.name = "宋体"
        style._element.rPr.rFonts.set(qn("w:eastAsia"), "宋体")
        style.font.size = Pt(10.5)
        style.font.color.rgb = RGBColor(0, 0, 0)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(8)
    p.paragraph_format.space_after = Pt(6)
    r = p.add_run(title)
    set_run_font(r, 21, True, "000000", "黑体")
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_after = Pt(15)
    r = p.add_run(subtitle)
    set_run_font(r, 9.5, False)

    footer = sec.footer.paragraphs[0]
    footer.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = footer.add_run("箱包 MES 小组项目  |  分组任务14")
    set_run_font(r, 8, False, "555555")
    footer.add_run("    第 ")
    field = OxmlElement("w:fldSimple")
    field.set(qn("w:instr"), "PAGE")
    footer._p.append(field)
    footer.add_run(" 页")
    return doc


def build_erd():
    image = Image.new("RGB", (1500, 820), "white")
    draw = ImageDraw.Draw(image)
    try:
        title_f = ImageFont.truetype(r"C:\Windows\Fonts\msyhbd.ttc", 34)
        head_f = ImageFont.truetype(r"C:\Windows\Fonts\msyhbd.ttc", 25)
        body_f = ImageFont.truetype(r"C:\Windows\Fonts\msyh.ttc", 20)
    except OSError:
        title_f = head_f = body_f = ImageFont.load_default()
    draw.text((50, 26), "箱包 MES 日志与核心业务实体关系图", font=title_f, fill="#111827")
    boxes = {
        "product_sku": (70, 160, 440, 390, ["sku_id PK", "sku_code", "style_code"]),
        "work_order": (560, 160, 930, 390, ["work_order_id PK", "work_order_no", "sku_id FK"]),
        "production_report": (1050, 160, 1430, 390, ["report_id PK", "work_order_id FK", "report_time", "good_qty"]),
        "operation_log": (280, 520, 650, 760, ["log_id PK", "trace_id", "module_name", "operation_type"]),
        "api_monitor_log": (850, 520, 1220, 760, ["monitor_id PK", "trace_id", "status_code", "duration_ms"]),
    }
    for name, (x1, y1, x2, y2, fields) in boxes.items():
        draw.rounded_rectangle((x1, y1, x2, y2), radius=14, outline="#334155", width=3, fill="#FFFFFF")
        draw.text((x1 + 18, y1 + 12), name, font=head_f, fill="#000000")
        for i, field in enumerate(fields):
            draw.text((x1 + 20, y1 + 58 + i * 35), field, font=body_f, fill="#111827")

    def arrow(start, end, label):
        draw.line((start, end), fill="#475569", width=4)
        draw.polygon([end, (end[0] - 14, end[1] - 9), (end[0] - 14, end[1] + 9)], fill="#475569")
        draw.text(((start[0] + end[0]) // 2 - 20, (start[1] + end[1]) // 2 - 28), label,
                  font=body_f, fill="#111827")

    arrow((440, 275), (560, 275), "1 : N")
    arrow((930, 275), (1050, 275), "1 : N")
    draw.line((650, 640, 850, 640), fill="#475569", width=4)
    draw.text((683, 600), "trace_id 逻辑关联", font=body_f, fill="#111827")
    draw.text((70, 770), "业务表之间由外键关联；两张日志表不依赖业务外键，通过 trace_id 进行请求级逻辑关联。",
              font=body_f, fill="#374151")
    ERD.parent.mkdir(parents=True, exist_ok=True)
    image.save(ERD)


def create_design_doc():
    doc = setup_doc("箱包 MES 系统日志及监控设计方案", "任务10设计修订版  |  基于任务14采用 SLF4J 与 Logback")
    heading(doc, "1 设计目标与范围", 1)
    add_text(doc, "本方案为箱包 MES 项目统一运行日志的记录方式、等级、字段规范和归档规则。任务10初版使用自定义 AOP 将接口日志写入数据库；根据任务14要求，当前项目取消该自动采集切面，改由 SLF4J + Logback 记录到控制台和滚动文件。operation_log、api_monitor_log 表及分页查询接口保留，用于任务10既有数据的检索，不再由任务14运行日志自动写入。")
    add_text(doc, "覆盖应用启动、工单与排产、物料、报工、质量、外协、入库、包装追溯和 HTTP 接口等模块。当前代码先实现公共 HTTP 请求记录和工单分页查询日志，其余业务模块按同一规范接入。")

    heading(doc, "2 方案比较与最终选择", 1)
    add_table(doc, ["方案", "优点", "限制"], [
        ["自定义 AOP + 数据库", "横切逻辑集中；审计字段可查询；适合数据库审计", "依赖注解与切面；每次调用写库；与业务数据库耦合"],
        ["SLF4J + Logback", "统一门面；按级别过滤；支持控制台、滚动文件和采集平台", "默认日志在文件中；复杂检索需集中日志平台"],
        ["外部 APM 平台", "跨服务聚合、检索和告警能力强", "需要部署及账号配置，本课程本地项目条件不足"],
    ])
    add_text(doc, "最终选择 SLF4J + Logback 作为项目运行日志方案，符合任务14指定技术，避免逐个接口添加 AOP 注解。数据库日志表继续服务历史审计查询；两种日志的目标和数据流在文档中分开说明。")

    heading(doc, "3 日志模块与内容", 1)
    add_table(doc, ["模块", "应记录事件", "建议级别与主要字段"], [
        ["系统运行", "启动、关闭、配置缺失、依赖连接状态", "INFO；应用名、环境、启动结果；失败用ERROR"],
        ["HTTP接口", "请求完成、状态码、耗时、异常、慢请求", "INFO/WARN/ERROR；traceId、method、path、status、durationMs"],
        ["工单与排产", "工单分页、排产方案生成/发布、状态变化", "INFO记录结果摘要；DEBUG记录安全筛选条件；ERROR记录异常"],
        ["物料与库存", "领料、退料、库存扣减、批次锁定", "INFO记录单据号/批次号/结果；WARN记录库存不足"],
        ["报工与计件", "工序报工、良品/返工/报废数量、工资汇总", "INFO记录业务号和数量摘要；ERROR记录校验或持久化失败"],
        ["质量检验", "首检、巡检、终检、不合格处置", "INFO记录工单/批次/检验结论；WARN记录不合格"],
        ["外协与入库", "外协发出/回收、供应商回传、成品入库", "INFO记录业务单号与数量；WARN记录回传超时"],
        ["包装与追溯", "装箱、箱码关联、追溯查询", "INFO记录箱码/关联结果摘要；异常用WARN或ERROR"],
        ["权限与认证", "登录失败、越权访问、角色授权调整", "WARN记录失败类型和主体ID；禁止记录凭证"],
    ])
    add_text(doc, "日志应表达发生了什么、在哪个模块、结果如何以及如何关联请求。默认不记录完整请求/响应对象、完整SQL参数或批量明细；大批量任务记录批次号、汇总数量和结果。")

    heading(doc, "4 日志级别定义", 1)
    add_table(doc, ["级别", "使用规则", "MES 示例"], [
        ["ERROR", "操作失败且需处理；输出异常堆栈", "数据库不可用、关键业务保存失败"],
        ["WARN", "异常状态但可恢复，或性能超过阈值", "慢请求、库存不足、HTTP 4xx/5xx"],
        ["INFO", "关键业务事件、请求完成、启动关闭", "工单查询完成、报工提交成功"],
        ["DEBUG", "开发调试细节；生产默认关闭", "工单分页状态、优先级等筛选条件"],
        ["TRACE", "极细粒度调用信息；短时排障使用", "内部方法执行细节"],
    ])
    add_text(doc, "代码使用参数化占位符，异常对象作为最后一个参数。正常业务事件使用 INFO，避免将每条循环数据写入 INFO；阈值可配置，不应把不同接口的 SLA 强行统一为一个业务标准。")

    heading(doc, "5 日志内容与安全规范", 1)
    add_table(doc, ["内容", "规范"], [
        ["公共字段", "时间、级别、线程、traceId、Logger名称、消息"],
        ["接口字段", "HTTP方法、去除查询串的URI、状态码、耗时；响应头返回X-Trace-Id"],
        ["业务字段", "模块、业务单号/批次号、动作、结果摘要、数量摘要；仅选必要字段"],
        ["异常字段", "异常类型、简短原因和堆栈；不复制完整请求正文"],
        ["禁止字段", "密码、token、secret、Cookie、Authorization、密钥和不必要个人信息"],
        ["格式", "UTF-8；SLF4J参数化消息；每行可读；不以字符串拼接构造日志"],
    ])
    add_text(doc, "HTTP过滤器仅记录 request.getRequestURI()，不写 query string。DEBUG/TRACE 也必须遵守相同脱敏约束。日志用于运维和排障，不提交到Git。")

    heading(doc, "6 框架配置与代码位置", 1)
    add_table(doc, ["位置", "职责"], [
        ["pom.xml", "spring-boot-starter-logging；提供SLF4J API及Logback实现；移除spring-boot-starter-aop"],
        ["logback-spring.xml", "控制台和文件Appender；日期/大小滚动、压缩、保留天数与容量上限"],
        ["RequestTraceLoggingFilter", "生成请求traceId，写入MDC和响应头，记录状态/耗时/异常，结束后清理MDC"],
        ["WorkOrderServiceImpl", "用LoggerFactory记录工单查询开始、完成和异常"],
        ["application.yml", "慢请求阈值mes.monitor.slow-threshold-ms，默认1000ms"],
    ])
    add_bullets(doc, [
        "控制台输出：本地开发和容器标准输出。",
        "文件输出：logs/bag-mes.log；每天滚动，单文件10MB切分并压缩，保留30天，总量不超过1GB。",
        "MDC traceId：一次HTTP请求内过滤器和业务服务日志共用同一标识；响应头X-Trace-Id便于接口排查。",
        "慢请求：durationMs达到配置阈值输出WARN；默认1000ms，可通过mes.monitor.slow-threshold-ms调整。",
    ])

    heading(doc, "7 数据库关系与日志表边界", 1)
    doc.add_picture(str(ERD), width=Inches(6.8))
    add_text(doc, "业务关系：product_sku 1:N work_order；work_order 1:N production_report。operation_log 和 api_monitor_log 不引用工单外键，通过 trace_id 逻辑关联同一次请求。任务14新增的 SLF4J 文件日志通过 MDC traceId 关联请求内日志行，不自动写入数据库日志表。")
    add_text(doc, "operation_log 保存任务10既有业务操作日志字段；api_monitor_log 保存既有接口状态、耗时、慢请求和异常字段。两表的DDL与分页查询维持原状，既有数据可以继续查询。")

    heading(doc, "8 运行与验收", 1)
    add_numbered(doc, [
        "设置MES_DB_PORT、MES_DB_USERNAME和MES_DB_PASSWORD后，使用 ./mvnw.cmd spring-boot:run 启动项目。",
        "访问工单分页接口，例如 GET /work-orders/page?pageNum=1&pageSize=10&status=RUNNING。",
        "检查控制台及 logs/bag-mes.log，确认出现HTTP请求完成与工单查询完成日志、traceId一致。",
        "确认AOP注解与切面代码已移除，POM不再引入spring-boot-starter-aop。",
        "调用任务10的 /operation-logs/page 和 /api-monitor-logs/page，确认数据库历史/演示数据仍可查询。",
    ])
    add_text(doc, "自动化测试范围和用例见《箱包MES自动化测试方案》。运行日志是否落入文件、traceId是否关联、慢请求及异常分支均需在实际运行后记录测试结果。")
    doc.save(DESIGN)


def create_test_plan():
    doc = setup_doc("箱包 MES 系统自动化测试方案", "分组任务14  |  SLF4J 与 Logback 日志方案")
    heading(doc, "1 测试目标与范围", 1)
    add_text(doc, "本方案验证任务14新增的SLF4J + Logback日志配置、HTTP请求traceId关联、工单服务日志和敏感信息约束，并回归任务10数据库日志查询能力。本文是测试设计与实施计划，执行结果应由小组在本地环境运行后填写；本方案不虚构测试通过记录。")
    add_table(doc, ["范围内", "本次不覆盖"], [[
        "依赖与Logback配置；日志等级和文件滚动；traceId/MDC；工单查询日志；异常、慢请求、安全字段；任务10日志分页查询",
        "生产环境压力容量认证；外部集中日志平台；实际全链路业务模块的部署验收；生产数据验证",
    ]])

    heading(doc, "2 环境与测试数据", 1)
    add_table(doc, ["项目", "要求"], [
        ["运行环境", "Java 21、Spring Boot 3.5.5、MySQL 8.0+、Maven Wrapper"],
        ["数据库", "本地bag_mes测试库；导入bag_mes_schema.sql和必要演示数据；不使用生产数据"],
        ["应用配置", "MES_DB_PORT、MES_DB_USERNAME、MES_DB_PASSWORD；默认慢请求阈值1000ms"],
        ["日志输出", "控制台及logs/bag-mes.log；轮转归档目录logs/archive"],
        ["隔离要求", "测试使用固定业务样例；运行前备份或使用专用测试库；不向日志写入真实凭证"],
    ])

    heading(doc, "3 覆盖标准", 1)
    add_bullets(doc, [
        "新增RequestTraceLoggingFilter与日志配置至少达到行覆盖率80%、分支覆盖率70%。",
        "过滤器的成功、异常、慢请求、HTTP错误状态、MDC清理等关键分支覆盖率达到100%。",
        "关键业务服务的正常、无结果、异常三类行为均有自动化用例；分页边界覆盖页码和页大小下限/上限。",
        "安全用例必须确认日志消息和文件不包含query string、密码、token、secret、Cookie或Authorization值。",
        "每个失败用例须保留失败原因；缺陷修复后重跑相关用例及回归集。",
    ])

    heading(doc, "4 单元测试用例", 1)
    add_table(doc, ["编号", "测试点", "输入/操作", "预期结果", "建议方式"], [
        ["UT-01", "traceId生成", "发起一个HTTP请求", "响应头含非空X-Trace-Id；MDC有对应值", "MockMvc + Filter测试"],
        ["UT-02", "MDC清理", "请求正常返回后检查当前线程MDC", "traceId已移除，避免线程复用串号", "Filter单元测试"],
        ["UT-03", "成功请求日志", "返回HTTP 200", "输出method、path、status、duration和traceId", "ListAppender/OutputCapture"],
        ["UT-04", "异常请求日志", "Filter chain抛出异常", "输出ERROR与异常堆栈；异常继续向上抛出", "Filter单元测试"],
        ["UT-05", "慢请求阈值", "耗时低于/等于阈值", "低于阈值INFO；达到阈值WARN", "可注入时钟或可控Filter chain"],
        ["UT-06", "HTTP错误状态", "响应状态设为4xx或5xx", "输出WARN，带状态码", "MockHttpServletResponse"],
        ["UT-07", "工单查询成功", "Mapper返回分页结果", "INFO日志包含页码、数量和耗时，结果未改变", "Mockito + 捕获Logback事件"],
        ["UT-08", "工单查询失败", "Mapper抛出运行时异常", "ERROR记录异常堆栈，原异常向上抛出", "Mockito + assertThrows"],
        ["UT-09", "参数化格式", "检查固定业务日志模板", "占位符格式可读；不拼接请求对象", "代码检查+日志事件断言"],
    ])

    heading(doc, "5 接口与集成测试用例", 1)
    add_table(doc, ["编号", "接口/场景", "操作步骤", "预期结果"], [
        ["API-01", "工单分页正常", "GET /work-orders/page?pageNum=1&pageSize=10", "HTTP 200；响应结构正常；响应头traceId与应用日志一致"],
        ["API-02", "工单多条件查询", "增加status、priority或workOrderNo条件", "筛选正确；日志只记录受限条件，不含完整query string"],
        ["API-03", "分页边界", "pageNum=0、pageSize=0或pageSize>100", "行为符合现有分页规范；无SQL错误"],
        ["API-04", "慢请求", "测试环境人为延迟超过配置阈值", "日志级别为WARN，记录durationMs与traceId"],
        ["API-05", "未处理异常", "调用预置测试端点/模拟服务异常", "日志出现ERROR堆栈；请求结果仍按异常处理器规则返回"],
        ["API-06", "敏感信息排查", "请求中带虚构token/password测试值", "日志文件全文搜索不到敏感值；日志URI无查询串"],
        ["API-07", "任务10日志查询回归", "查询/operation-logs/page及/api-monitor-logs/page", "可分页读取数据库已有日志；不要求新请求自动落库"],
        ["API-08", "文件归档配置", "在测试环境触发大小/日期轮转", "当前日志切换到新文件，旧文件压缩并遵守保留上限"],
    ])

    heading(doc, "6 自动化测试实施方案", 1)
    add_numbered(doc, [
        "使用JUnit 5与Spring Boot Test；过滤器测试使用MockHttpServletRequest、MockHttpServletResponse和MockFilterChain。",
        "使用Logback ListAppender或Spring Boot OutputCaptureExtension捕获日志事件，断言级别、traceId、消息字段和异常堆栈。",
        "使用Mockito模拟WorkOrderMapper，分别构造成功分页、空结果和异常；确认服务返回值及异常语义不变。",
        "使用MockMvc调用HTTP接口，校验状态码、响应头X-Trace-Id和参数边界；对文件内容做敏感关键词扫描。",
        "数据库回归测试使用专用MySQL测试库并准备operation_log、api_monitor_log演示记录，验证分页查询；清理测试数据时限定测试traceId前缀。",
        "Logback滚动策略通过短期测试配置降低文件大小阈值或注入测试配置验证，禁止为测试临时改动生产默认保留策略。",
    ])
    add_text(doc, "建议命令：./mvnw.cmd -Dtest=RequestTraceLoggingFilterTest,WorkOrderLoggingTest test；接口/数据库集成测试：./mvnw.cmd -Dtest=LogMonitoringIntegrationTest test；完整回归：./mvnw.cmd test。命令应在项目根目录运行。")

    heading(doc, "7 执行流程与准入准出", 1)
    add_numbered(doc, [
        "检查Java、MySQL和环境变量，确认连接的是本地测试库。",
        "确认POM包含spring-boot-starter-logging且不再包含spring-boot-starter-aop。",
        "启动应用，执行单元测试、API测试和数据库分页回归测试。",
        "分别检查控制台和logs/bag-mes.log，核对traceId、INFO/WARN/ERROR分支及敏感字段。",
        "记录执行时间、命令、通过/失败数量、失败原因和截图；缺陷修复后执行回归。",
    ])
    add_table(doc, ["进入条件", "完成条件"], [[
        "测试库可连接；代码编译依赖可用；测试配置已确认；用例数据为虚构或专用数据",
        "关键分支用例全部通过；覆盖率达到本方案基线；无敏感字段泄漏；历史日志查询回归通过；失败项有缺陷记录",
    ]])

    heading(doc, "8 结果记录表", 1)
    add_text(doc, "本交付物为测试方案，尚未执行上述测试。小组完成本机测试后填写下表，并附真实运行截图。")
    add_table(doc, ["日期/环境", "执行命令", "通过/总数", "覆盖率", "结论/缺陷编号"], [
        ["待填写", "待填写", "待填写", "待填写", "待填写"],
        ["", "", "", "", ""],
    ])
    doc.save(TESTPLAN)


if __name__ == "__main__":
    DOCS.mkdir(parents=True, exist_ok=True)
    build_erd()
    create_design_doc()
    create_test_plan()
    print(DESIGN)
    print(TESTPLAN)
