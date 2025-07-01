package com.gr.geias.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class JobFairWithCompaniesDTO {
    private Integer jobFairId;
    private String title;
    private String location;
    private Date startTime;
    private Date endTime;

    private List<CompanySimpleDTO> companies;
}
