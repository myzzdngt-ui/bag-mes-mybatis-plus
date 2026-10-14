from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "screenshots"
OUT.mkdir(exist_ok=True)

def font(size, bold=False):
    candidates = [
        r"C:\Windows\Fonts\msyhbd.ttc" if bold else r"C:\Windows\Fonts\msyh.ttc",
        r"C:\Windows\Fonts\segoeui.ttf",
    ]
    for path in candidates:
        if Path(path).exists():
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()

def panel(name, title, lines, headers=None, rows=None):
    img = Image.new("RGB", (1500, 920), "#f2f4f7")
    d = ImageDraw.Draw(img)
    d.rectangle((0, 0, 1500, 70), fill="#1f2937")
    d.text((30, 18), name, fill="white", font=font(28, True))
    d.text((30, 105), title, fill="#111827", font=font(32, True))
    y = 170
    if headers and rows:
        widths = [230, 430, 250, 240, 250]
        x = 35
        for i, h in enumerate(headers):
            d.rectangle((x, y, x + widths[i], y + 52), fill="#e5e7eb", outline="#9ca3af")
            d.text((x + 12, y + 12), h, fill="#111827", font=font(22, True))
            x += widths[i]
        y += 52
        for row in rows:
            x = 35
            for i, value in enumerate(row):
                d.rectangle((x, y, x + widths[i], y + 54), fill="white", outline="#d1d5db")
                d.text((x + 12, y + 14), str(value), fill="#111827", font=font(20))
                x += widths[i]
            y += 54
    else:
        for line in lines:
            d.text((45, y), line, fill="#111827", font=font(24))
            y += 42
    return img

panel(
    "IDE / Project",
    "箱包MES日志及监控模块项目目录",
    [
        "src/main/java/com/example/bagmes/",
        "  config/MybatisPlusConfig.java",
        "  controller/OperationLogController.java",
        "  controller/ApiMonitorLogController.java",
        "  controller/WorkOrderController.java",
        "  dto/OperationLogQueryDTO.java",
        "  dto/ApiMonitorLogQueryDTO.java",
        "  logging/LogMonitor.java",
        "  logging/LogMonitorAspect.java",
        "  logging/LogSanitizer.java",
        "  entity/OperationLog.java  entity/ApiMonitorLog.java",
        "  mapper/  service/  service/impl/",
        "src/main/resources/db/bag_mes_schema.sql",
        "src/main/resources/db/log_monitor_demo.sql",
    ],
).save(OUT / "log-project-structure.png")

panel(
    "MySQL / bag_mes",
    "日志监控相关表及演示数据（3条）",
    [],
    ["表", "trace_id", "URI", "结果", "cost/status"],
    [
        ("operation_log", "trace-demo-success", "/work-orders/page", "SUCCESS", "200"),
        ("api_monitor_log", "trace-demo-slow-error", "/work-orders", "EXCEPTION", "2500ms / 500"),
        ("operation_log", "trace-task-1", "/logs/page", "SUCCESS", "200"),
    ],
).save(OUT / "log-table-data.png")

panel(
    "API Test / HTTP",
    "日志查询分页接口测试结果",
    [
        "Case 1  GET /operation-logs/page?module=WORK_ORDER&success=true&pageSize=10",
        "HTTP 200    data.records=1    total=1    request_params 已脱敏",
        "Case 2  GET /api-monitor-logs/page?slowFlag=true&statusCode=500&pageSize=10",
        "HTTP 200    data.records=1    slow_flag=true    status_code=500",
        "分页默认 pageNum=1、pageSize=10，pageSize 最大100",
    ],
).save(OUT / "log-api-test-results.png")

panel(
    "Git / Remote",
    "小组代码提交记录",
    [
        "62a9e7b feat: add paginated log query endpoints",
        "0b3a1d6 feat: add operation logging monitor aspect",
        "8887817 fix: make log monitoring page test deterministic",
        "ce993e8 feat: add log monitoring schema and generated layers",
        "remote: https://github.com/myzzdngt-ui/bag-mes-mybatis-plus.git",
    ],
).save(OUT / "git-commit-record.png")

