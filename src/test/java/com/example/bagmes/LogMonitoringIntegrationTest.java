package com.example.bagmes;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.bagmes.entity.ApiMonitorLog;
import com.example.bagmes.entity.OperationLog;
import com.example.bagmes.service.ApiMonitorLogService;
import com.example.bagmes.service.OperationLogService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LogMonitoringIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ApiMonitorLogService apiMonitorLogService;

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
}
