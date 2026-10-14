package com.example.bagmes;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bagmes.entity.ApiMonitorLog;
import com.example.bagmes.entity.OperationLog;
import com.example.bagmes.logging.LogMonitor;
import com.example.bagmes.service.ApiMonitorLogService;
import com.example.bagmes.service.OperationLogService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(LogMonitoringIntegrationTest.ThrowingLogController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogMonitoringIntegrationTest {

    private static final String WORK_ORDER_MODULE = "WORK_ORDER";
    private static final String TEST_MODULE = "TEST_LOGGING";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ApiMonitorLogService apiMonitorLogService;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @BeforeAll
    void seedDemoData() {
        jdbcTemplate.update("DELETE FROM operation_log WHERE trace_id LIKE ?", "trace-task-1-other-%");
        jdbcTemplate.update("DELETE FROM api_monitor_log WHERE trace_id LIKE ?", "trace-task-1-other-%");
        jdbcTemplate.update("DELETE FROM operation_log WHERE trace_id = ?", "trace-task-1");
        jdbcTemplate.update("DELETE FROM api_monitor_log WHERE trace_id = ?", "trace-task-1");
        for (int i = 1; i <= 11; i++) {
            String traceId = "trace-task-1-other-" + i;
            jdbcTemplate.update("""
                    INSERT INTO operation_log
                        (trace_id, module_name, operation_type, operation_name, request_method, request_uri,
                         request_params, operator_id, operator_name, success, response_code, client_ip, user_agent)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, traceId, "other", "TEST", "other log", "GET", "/other",
                    "{}", 2L, "other", true, 200, "127.0.0.1", "integration-test");
            jdbcTemplate.update("""
                    INSERT INTO api_monitor_log
                        (trace_id, request_method, request_uri, handler_method, status_code, duration_ms,
                         slow_flag, exception_class, exception_message)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """, traceId, "GET", "/other", "OtherHandler.handle", 200, 10L, false, "", "");
        }
        jdbcTemplate.update("""
                INSERT INTO operation_log
                    (trace_id, module_name, operation_type, operation_name, request_method, request_uri,
                     request_params, operator_id, operator_name, success, response_code, client_ip, user_agent)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, "trace-task-1", "log-monitoring", "TEST", "page logs", "GET", "/logs/page",
                "{\"page\":1}", 1L, "test", true, 200, "127.0.0.1", "integration-test");
        jdbcTemplate.update("""
                INSERT INTO api_monitor_log
                    (trace_id, request_method, request_uri, handler_method, status_code, duration_ms,
                     slow_flag, exception_class, exception_message)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """, "trace-task-1", "GET", "/logs/page", "LogMonitoringIntegrationTest.pageLogs",
                500, 1500L, true, "", "");
    }

    @Test
    void pagesThroughOperationAndApiMonitorLogs() {
        IPage<OperationLog> operationPage = operationLogService.page(
                new Page<>(1, 10),
                Wrappers.<OperationLog>lambdaQuery()
                        .eq(OperationLog::getTraceId, "trace-task-1")
                        .orderByAsc(OperationLog::getLogId));
        IPage<ApiMonitorLog> monitorPage = apiMonitorLogService.page(
                new Page<>(1, 10),
                Wrappers.<ApiMonitorLog>lambdaQuery()
                        .eq(ApiMonitorLog::getTraceId, "trace-task-1")
                        .orderByAsc(ApiMonitorLog::getMonitorId));

        assertThat(operationPage.getRecords()).hasSize(1);
        assertThat(operationPage.getRecords())
                .extracting(OperationLog::getTraceId)
                .contains("trace-task-1");
        assertThat(monitorPage.getRecords()).hasSize(1);
        assertThat(monitorPage.getRecords())
                .extracting(ApiMonitorLog::getTraceId)
                .contains("trace-task-1");
    }

    @Test
    void capturesSuccessfulWorkOrderRequestInBothLogs() {
        long operationCount = operationLogService.count(Wrappers.<OperationLog>lambdaQuery()
                .eq(OperationLog::getModuleName, WORK_ORDER_MODULE));

        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/work-orders/page?pageNum=1&pageSize=10&token=work-order-token",
                String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(operationLogService.count(Wrappers.<OperationLog>lambdaQuery()
                .eq(OperationLog::getModuleName, WORK_ORDER_MODULE))).isEqualTo(operationCount + 1);

        OperationLog operationLog = latestOperationLog(WORK_ORDER_MODULE);
        ApiMonitorLog monitorLog = latestMonitorLog(operationLog.getTraceId());
        assertThat(operationLog.getOperationType()).isEqualTo("QUERY");
        assertThat(operationLog.getOperationName()).isEqualTo("工单分页查询");
        assertThat(operationLog.getSuccess()).isTrue();
        assertThat(operationLog.getResponseCode()).isEqualTo(200);
        assertThat(operationLog.getRequestParams()).contains("***").doesNotContain("work-order-token");
        assertThat(monitorLog.getTraceId()).isEqualTo(operationLog.getTraceId());
        assertThat(monitorLog.getStatusCode()).isEqualTo(200);
        assertThat(monitorLog.getExceptionClass()).isNullOrEmpty();
    }

    @Test
    void capturesExceptionWithoutReplacingTheOriginalFailure() {
        long operationCount = operationLogService.count(Wrappers.<OperationLog>lambdaQuery()
                .eq(OperationLog::getModuleName, TEST_MODULE));

        var response = restTemplate.getForEntity(
                "http://localhost:" + port + "/test/log-monitoring/fail?password=very-secret&Token=token-secret&secret=secret-value&Authorization=bearer-value",
                String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(operationLogService.count(Wrappers.<OperationLog>lambdaQuery()
                .eq(OperationLog::getModuleName, TEST_MODULE))).isEqualTo(operationCount + 1);

        OperationLog operationLog = latestOperationLog(TEST_MODULE);
        ApiMonitorLog monitorLog = latestMonitorLog(operationLog.getTraceId());
        assertThat(operationLog.getSuccess()).isFalse();
        assertThat(operationLog.getResponseCode()).isEqualTo(500);
        assertThat(operationLog.getErrorMessage()).contains("test logging failure");
        assertThat(operationLog.getRequestParams()).contains("***")
                .doesNotContain("very-secret", "token-secret", "secret-value", "bearer-value");
        assertThat(monitorLog.getTraceId()).isEqualTo(operationLog.getTraceId());
        assertThat(monitorLog.getStatusCode()).isEqualTo(500);
        assertThat(monitorLog.getExceptionClass()).isEqualTo(IllegalStateException.class.getName());
        assertThat(monitorLog.getExceptionMessage()).isEqualTo("test logging failure");
    }

    private OperationLog latestOperationLog(String module) {
        return operationLogService.getOne(Wrappers.<OperationLog>lambdaQuery()
                .eq(OperationLog::getModuleName, module)
                .orderByDesc(OperationLog::getLogId)
                .last("LIMIT 1"));
    }

    private ApiMonitorLog latestMonitorLog(String traceId) {
        return apiMonitorLogService.getOne(Wrappers.<ApiMonitorLog>lambdaQuery()
                .eq(ApiMonitorLog::getTraceId, traceId)
                .orderByDesc(ApiMonitorLog::getMonitorId)
                .last("LIMIT 1"));
    }

    @RestController
    @RequestMapping("/test/log-monitoring")
    static class ThrowingLogController {

        @GetMapping("/fail")
        @LogMonitor(module = TEST_MODULE, operation = "THROW", name = "日志异常测试")
        public void fail(@RequestParam String password) {
            throw new IllegalStateException("test logging failure");
        }
    }
}
