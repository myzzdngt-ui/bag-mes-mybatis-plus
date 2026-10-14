USE bag_mes;

INSERT INTO operation_log
  (trace_id, module_name, operation_type, operation_name, request_method, request_uri,
   request_params, operator_id, operator_name, success, response_code, client_ip, user_agent)
VALUES
  ('trace-demo-success', 'work-order', 'QUERY', '查询工单', 'GET', '/work-orders/page',
   JSON_OBJECT('pageNum', 1, 'pageSize', 10), 1001, 'demo-user', 1, 200, '127.0.0.1', 'demo-client');

INSERT INTO api_monitor_log
  (trace_id, request_method, request_uri, handler_method, status_code, duration_ms,
   slow_flag, exception_class, exception_message)
VALUES
  ('trace-demo-slow-error', 'POST', '/work-orders',
   'com.example.bagmes.controller.WorkOrderController#create', 500, 2500, 1,
   'RuntimeException', 'demo slow request failure');
