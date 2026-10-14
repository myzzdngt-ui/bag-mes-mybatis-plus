package com.example.bagmes.logging;

import com.example.bagmes.entity.ApiMonitorLog;
import com.example.bagmes.entity.OperationLog;
import com.example.bagmes.service.ApiMonitorLogService;
import com.example.bagmes.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Persists business operation and API monitoring records without changing controller behavior.
 */
@Aspect
@Component
public class LogMonitorAspect {

    private final OperationLogService operationLogService;
    private final ApiMonitorLogService apiMonitorLogService;
    private final long slowThresholdMs;

    public LogMonitorAspect(OperationLogService operationLogService,
                            ApiMonitorLogService apiMonitorLogService,
                            @Value("${mes.monitor.slow-threshold-ms:1000}") long slowThresholdMs) {
        this.operationLogService = operationLogService;
        this.apiMonitorLogService = apiMonitorLogService;
        this.slowThresholdMs = slowThresholdMs;
    }

    @Around("@annotation(logMonitor)")
    public Object capture(ProceedingJoinPoint joinPoint, LogMonitor logMonitor) throws Throwable {
        HttpServletRequest request = currentRequest();
        String traceId = UUID.randomUUID().toString();
        long startedAt = System.nanoTime();

        try {
            Object result = joinPoint.proceed();
            persist(joinPoint, logMonitor, request, traceId, elapsedMs(startedAt), true, null);
            return result;
        } catch (Throwable throwable) {
            persist(joinPoint, logMonitor, request, traceId, elapsedMs(startedAt), false, throwable);
            throw throwable;
        }
    }

    private void persist(ProceedingJoinPoint joinPoint, LogMonitor logMonitor, HttpServletRequest request,
                         String traceId, long durationMs, boolean success, Throwable throwable) {
        try {
            operationLogService.save(operationLog(logMonitor, request, traceId, success, throwable));
        } catch (Exception ignored) {
            // Monitoring must never replace the business result or exception.
        }
        try {
            apiMonitorLogService.save(apiMonitorLog(joinPoint, request, traceId, durationMs, success, throwable));
        } catch (Exception ignored) {
            // Monitoring must never replace the business result or exception.
        }
    }

    private OperationLog operationLog(LogMonitor logMonitor, HttpServletRequest request, String traceId,
                                      boolean success, Throwable throwable) {
        OperationLog log = new OperationLog();
        log.setTraceId(traceId);
        log.setModuleName(logMonitor.module());
        log.setOperationType(logMonitor.operation());
        log.setOperationName(logMonitor.name());
        log.setRequestMethod(requestMethod(request));
        log.setRequestUri(requestUri(request));
        log.setRequestParams(LogSanitizer.sanitize(request == null ? null : request.getParameterMap()));
        log.setSuccess(success);
        log.setResponseCode(success ? 200 : 500);
        log.setErrorMessage(throwable == null ? null : throwable.getClass().getName() + ": " + throwable.getMessage());
        log.setClientIp(clientIp(request));
        log.setUserAgent(request == null ? null : request.getHeader("User-Agent"));
        return log;
    }

    private ApiMonitorLog apiMonitorLog(ProceedingJoinPoint joinPoint, HttpServletRequest request, String traceId,
                                        long durationMs, boolean success, Throwable throwable) {
        ApiMonitorLog log = new ApiMonitorLog();
        log.setTraceId(traceId);
        log.setRequestMethod(requestMethod(request));
        log.setRequestUri(requestUri(request));
        log.setHandlerMethod(((MethodSignature) joinPoint.getSignature()).getDeclaringTypeName()
                + "." + joinPoint.getSignature().getName());
        log.setStatusCode(success ? 200 : 500);
        log.setDurationMs(durationMs);
        log.setSlowFlag(durationMs >= slowThresholdMs);
        log.setExceptionClass(throwable == null ? null : throwable.getClass().getName());
        log.setExceptionMessage(throwable == null ? null : throwable.getMessage());
        return log;
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes == null ? null : attributes.getRequest();
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    private String requestMethod(HttpServletRequest request) {
        return request == null ? "UNKNOWN" : request.getMethod();
    }

    private String requestUri(HttpServletRequest request) {
        return request == null ? "" : request.getRequestURI();
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        return forwardedFor == null || forwardedFor.isBlank()
                ? request.getRemoteAddr()
                : forwardedFor.split(",")[0].trim();
    }
}
