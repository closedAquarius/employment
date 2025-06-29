package com.gr.geias.dto;


import lombok.Data;

@Data
public class JobFairAppliedDTO {
    private Integer jobFairId;
    private String title;
    private String location;
    private Integer boothLocation;
    private Integer status; // 0待审核，1通过，2拒绝
}