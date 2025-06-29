package com.gr.geias.controller;

import com.gr.geias.service.CompanyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companyinfo")
public class CompanyInfoController {

    @Autowired
    private CompanyInfoService companyInfoService;


}
