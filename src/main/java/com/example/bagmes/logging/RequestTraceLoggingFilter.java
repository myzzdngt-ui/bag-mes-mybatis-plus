package com.example.bagmes.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/** Adds a request-scoped trace id and logs the final HTTP outcome. */
@Component
public class RequestTraceLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestTraceLoggingFilter.class);
    private final long slowThresholdMs;

    public RequestTraceLoggingFilter(@Value("${mes.monitor.slow-threshold-ms:1000}") long slowThresholdMs) {
        this.slowThresholdMs = slowThresholdMs;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        long startedAt = System.nanoTime();
        boolean failed = false;
        MDC.put("traceId", traceId);
        response.setHeader("X-Trace-Id", traceId);
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException exception) {
            failed = true;
            long durationMs = elapsedMs(startedAt);
            log.error("HTTP请求异常 method={} path={} status={} durationMs={} exceptionType={}",
                    request.getMethod(), request.getRequestURI(), 500, durationMs,
                    exception.getClass().getSimpleName(), exception);
            throw exception;
        } finally {
            long durationMs = elapsedMs(startedAt);
            if (!failed && (durationMs >= slowThresholdMs || response.getStatus() >= 400)) {
                log.warn("HTTP请求完成 method={} path={} status={} durationMs={} slow={}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs,
                        durationMs >= slowThresholdMs);
            } else if (!failed) {
                log.info("HTTP请求完成 method={} path={} status={} durationMs={}",
                        request.getMethod(), request.getRequestURI(), response.getStatus(), durationMs);
            }
            MDC.remove("traceId");
        }
    }

    private long elapsedMs(long startedAt) {
        return (System.nanoTime() - startedAt) / 1_000_000;
    }
}
