package com.example.bagmes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkOrderPageQueryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    void seedDemoData() {
        jdbcTemplate.update("INSERT IGNORE INTO product_sku (sku_id, sku_code, style_code, color_code, spec_code, product_category) VALUES (1, 'SKU-BAG-001', 'STYLE-001', 'BLACK', 'M', 'SOFT_BAG')");
        jdbcTemplate.update("INSERT IGNORE INTO bom_version (bom_id, sku_id, bom_version, status) VALUES (1, 1, 'V1.0', 'APPROVED')");
        jdbcTemplate.update("INSERT IGNORE INTO routing_version (routing_id, sku_id, routing_version, status) VALUES (1, 1, 'V1.0', 'APPROVED')");
        jdbcTemplate.update("DELETE FROM work_order WHERE work_order_no IN ('WO20260901', 'WO20260902', 'WO20260903')");
        jdbcTemplate.update("INSERT INTO work_order (work_order_no, sku_id, bom_id, routing_id, plan_qty, status, priority, plan_start_time, plan_end_time) VALUES ('WO20260901', 1, 1, 1, 1000, 'RELEASED', 2, '2026-09-01 08:00:00', '2026-09-05 18:00:00')");
        jdbcTemplate.update("INSERT INTO work_order (work_order_no, sku_id, bom_id, routing_id, plan_qty, status, priority, plan_start_time, plan_end_time) VALUES ('WO20260902', 1, 1, 1, 500, 'RUNNING', 1, '2026-09-06 08:00:00', '2026-09-10 18:00:00')");
        jdbcTemplate.update("INSERT INTO work_order (work_order_no, sku_id, bom_id, routing_id, plan_qty, status, priority, plan_start_time, plan_end_time) VALUES ('WO20260903', 1, 1, 1, 300, 'RELEASED', 1, '2026-09-11 08:00:00', '2026-09-12 18:00:00')");
    }

    @Test
    void filtersByStatusAndPriorityWithPagination() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/work-orders/page?pageNum=1&pageSize=10&status=RELEASED&priority=2", String.class);

        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(body.path("data").path("total").asLong()).isEqualTo(1);
        assertThat(body.path("data").path("records").get(0).path("workOrderNo").asText()).isEqualTo("WO20260901");
    }

    @Test
    void filtersByStatusAndSkuWithPagination() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/work-orders/page?pageNum=1&pageSize=10&status=RUNNING&skuId=1", String.class);

        JsonNode body = objectMapper.readTree(response.getBody());
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(body.path("data").path("total").asLong()).isEqualTo(1);
        assertThat(body.path("data").path("records").get(0).path("workOrderNo").asText()).isEqualTo("WO20260902");
    }
}
