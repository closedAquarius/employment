package com.gr.geias.controller;

import com.gr.geias.model.CompanyInfo;
import com.gr.geias.model.PersonInfo;
import com.gr.geias.service.CompanyInfoService;
import com.gr.geias.service.PersonInfoService;
import com.gr.geias.util.TokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/companyinfo")
public class CompanyInfoController {

    @Autowired
    private CompanyInfoService companyInfoService;
    @Autowired
    private PersonInfoService personInfoService;

    //获取企业信息
    @GetMapping("/company-info")
    public Map<String, Object> getCompanyInfo(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        try {
            Claims claims = TokenUtil.extractClaims(request.getHeader("Authorization"));
            Integer userId = (Integer) claims.get("userId");
            Integer roles = (Integer) claims.get("roles");

            PersonInfo person = personInfoService.getPersonById(userId);
            CompanyInfo company = companyInfoService.getCompanyByPersonId(person.getPersonId());

            // 企业人员、管理员可查看无论是否确认的企业信息
            if (roles == 3||roles == 2) {
                map.put("success", true);
                map.put("company", company);
            }
            // 学生（roles == 0）或老师（roles == 1）只能查看已确认的企业信息
            else if (roles == 0 || roles == 1) {
                if (Boolean.TRUE.equals(company.getConfirmed())) {
                    map.put("success", true);
                    map.put("company", company);
                } else {
                    map.put("success", false);
                    map.put("errMsg", "企业信息未确认，禁止访问");
                }
            }
//            if (company == null || Boolean.FALSE.equals(company.getConfirmed())) {
//                map.put("success", false);
//                map.put("errMsg", "企业信息未确认，禁止访问");
//            } else {
//                map.put("success", true);
//                map.put("company", company);
//            }
//
//            map.put("success", true);
//            map.put("company", company);
//            测试成功

        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "token 解析失败：" + e.getMessage());
        }

        return map;
    }

    //修改企业信息
    @PostMapping("/update-company")
    public Map<String, Object> updateCompany(@RequestHeader("Authorization") String token,
                                             @RequestBody CompanyInfo companyInfo) {
        Map<String, Object> map = new HashMap<>();
        try {
            Claims claims = TokenUtil.extractClaims(token);
            Integer roles = (Integer) claims.get("roles");
            Integer userId = (Integer) claims.get("userId");

            if (roles != 3) {
                map.put("success", false);
                map.put("errMsg", "只有企业人员可以修改所属企业信息");
                return map;
            }

            // 防止篡改 companyId，强制绑定用户所属企业
            PersonInfo person = personInfoService.getPersonById(userId);
            companyInfo.setCompanyId(person.getCollegeId());
            companyInfo.setConfirmed(false);

            boolean updated = companyInfoService.updateCompany(companyInfo);

            if (updated) {
                map.put("success", true);
                map.put("msg", "修改成功，等待管理员确认");
            } else {
                map.put("success", false);
                map.put("errMsg", "修改失败");
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token 错误或过期：" + e.getMessage());
        }
        return map;
    }

    //管理员确认企业信息
    @PostMapping("/confirm-company")
    public Map<String, Object> confirmCompany(@RequestHeader("Authorization") String token,
                                              @RequestParam("companyId") Integer companyId) {
        Map<String, Object> map = new HashMap<>();
        try {
            Claims claims = TokenUtil.extractClaims(token);
            Integer roles = (Integer) claims.get("roles");

            if (roles != 2) {
                map.put("success", false);
                map.put("errMsg", "无权限确认企业信息");
                return map;
            }

            boolean confirmed = companyInfoService.confirmCompany(companyId);
            if (confirmed) {
                map.put("success", true);
            } else {
                map.put("success", false);
                map.put("errMsg", "确认失败");
            }
        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token 错误或过期：" + e.getMessage());
        }
        return map;
    }

    @PostMapping("/add-company")
    public Map<String, Object> addCompany(@RequestHeader("Authorization") String token,
                                          @RequestBody CompanyInfo companyInfo) {
        Map<String, Object> map = new HashMap<>();
        try {
            Claims claims = TokenUtil.extractClaims(token);
            Integer roles = (Integer) claims.get("roles");
            Integer userId = (Integer) claims.get("userId");

            if (roles != 3) {
                map.put("success", false);
                map.put("errMsg", "只有企业人员可以添加企业信息");
                return map;
            }

            // 设置默认未确认
            companyInfo.setConfirmed(false);

            // 添加企业信息（确保插入后 companyId 被自动回填）
            boolean added = companyInfoService.addCompany(companyInfo);

            if (added && companyInfo.getCompanyId() != null) {
                Integer newCompanyId = companyInfo.getCompanyId(); // 确保已回填主键
                System.out.println(companyInfo.getCompanyId());

                // 获取当前用户
                PersonInfo person = personInfoService.getPersonById(userId);
                if (person == null) {
                    map.put("success", false);
                    map.put("errMsg", "用户信息不存在");
                    return map;
                }

                // 更新用户 collegeId 为新公司 ID
                person.setCollegeId(newCompanyId);
                boolean updated = personInfoService.updatePerson(person);

                if (updated) {
                    map.put("success", true);
                    map.put("msg", "添加成功，已自动绑定企业信息，等待管理员确认");
                } else {
                    map.put("success", false);
                    map.put("errMsg", "企业添加成功，但用户信息更新失败");
                }
            } else {
                map.put("success", false);
                map.put("errMsg", "企业添加失败或 companyId 未生成");
            }

        } catch (Exception e) {
            map.put("success", false);
            map.put("errMsg", "Token 错误或处理异常：" + e.getMessage());
        }
        return map;
    }

}