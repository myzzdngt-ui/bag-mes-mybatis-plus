package com.example.bagmes.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class WorkOrderQueryDTO {

    private long pageNum = 1;
    private long pageSize = 10;
    private String workOrderNo;
    private String status;
    private Long skuId;
    private Byte priority;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planStartTimeBegin;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime planStartTimeEnd;
}
