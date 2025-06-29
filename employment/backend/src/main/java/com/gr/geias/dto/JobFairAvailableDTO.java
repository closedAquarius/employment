package com.gr.geias.dto;

import lombok.Data;

import java.util.List;


@Data
public class JobFairAvailableDTO {
    private Integer jobFairId;
    private String title;
    private String location;
    private Integer boothTotal;
    private List<Integer> occupiedBooths;  // 已被占用的展位编号（例如：[1, 3]）
}
