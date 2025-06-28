package com.gr.geias.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JobFairWithCompaniesDTO {
    private Integer jobFairId;
    private String title;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<CompanySimpleDTO> companies;
}
