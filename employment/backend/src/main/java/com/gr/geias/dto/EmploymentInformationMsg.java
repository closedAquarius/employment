package com.gr.geias.dto;

import com.gr.geias.model.EmploymentInformation;
import lombok.Data;

import java.util.List;

/**
 * 就业信息消息传输对象
 */
@Data
public class EmploymentInformationMsg {
    /**
     * 操作是否成功
     */
    private Boolean success;
    
    /**
     * 就业信息列表
     */
    private List<EmploymentInformation> list;
    
    /**
     * 总数
     */
    private Integer count;
} 