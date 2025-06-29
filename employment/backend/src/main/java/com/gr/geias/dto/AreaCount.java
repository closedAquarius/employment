package com.gr.geias.dto;

import lombok.Data;

/**
 * 区域统计DTO，用于区域统计相关数据传输
 */
@Data
public class AreaCount {
    /**
     * 区域名称
     */
    private String name;
    
    /**
     * 区域对应的值
     */
    private Integer value;
} 