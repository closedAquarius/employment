package com.gr.geias.dto;

import lombok.Data;

@Data
public class CompanySimpleDTO {
    private Integer companyId;
    private String companyName;
    private String companyIntro;
    private int boothLocation;
}
