package com.example.bagmes.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApiMonitorLogQueryDTO {

    private String requestUri;
    private String requestMethod;
    private Integer statusCode;
    private Boolean slowFlag;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtBegin;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAtEnd;

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public int normalizedPageNum() {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    public int normalizedPageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
