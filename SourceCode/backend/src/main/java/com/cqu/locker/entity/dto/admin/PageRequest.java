package com.cqu.locker.entity.dto.admin;

import lombok.Data;

/**
 * 分页请求基类
 */
@Data
public class PageRequest {
    /**
     * 当前页码（从1开始）
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer pageSize = 10;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向: asc/desc
     */
    private String sortOrder = "desc";
}
