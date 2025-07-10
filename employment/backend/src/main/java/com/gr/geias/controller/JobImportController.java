package com.gr.geias.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gr.geias.dto.NewsCommentDTO;
import com.gr.geias.model.JobPosting;
import com.gr.geias.service.JobImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobImportController {
    @Autowired
    JobImportService jobImportService;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 导入职位信
     * @return 导入结果
     */
    @PostMapping("/import")
    public ResponseEntity<String> importJobPostings() {
        try {
            System.out.println("=======================");
            System.out.println("已经进入后端");
            // 使用 ClassPathResource 来加载文件
            Resource resource = new ClassPathResource("data/all_data.json");
            if (!resource.exists()) {
                throw new FileNotFoundException("File not found: " + resource.getFile());
            }
            String classpathLocation = resource.getFile().getAbsolutePath();
//            String classpathLocation="../resources/data/all_data.json";
            jobImportService.importFromJson(classpathLocation);
            return ResponseEntity.ok("Job postings imported successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to import job postings: " + e.getMessage());
        }
    }

    /**
     * 根据jobId获取职位信息
     * @param jobId 专业ID
     * @return 职位信息列表
     */
    @GetMapping("/{jobId}/position")
    public ResponseEntity<Map<String, Object>> getComments(@RequestParam(name = "pageNum",defaultValue = "1") Integer page,
                                                           @RequestParam(name = "pageSize",defaultValue = "10") Integer size,
                                                           @PathVariable Integer jobId) {
        HashMap<String, Object> res = new HashMap<>();
        int offset = (page - 1) * size;
        System.out.println("size:" + size);
        System.out.println("offset:" + offset);
        System.out.println("page:" + page);
        System.out.println("jobId:" + jobId);
        List<JobPosting> position = jobImportService.getJobByJobId(jobId,offset, size);
        res.put("data", position);
        System.out.println("===================================");
        System.out.println("res的data值为" + res.get("data"));
        System.out.println("=====================================");
        int  count = jobImportService.findByIdCount(jobId);
        System.out.println("===================================");
        System.out.println("count:" + count);
        System.out.println("=====================================");
        res.put("total", count);
        String jsonStringWithDateFormat = JSON.toJSONStringWithDateFormat(res, "yyyy-MM-dd HH:mm:ss");
        return  ResponseEntity.ok(JSONObject.parseObject(jsonStringWithDateFormat));
    }

    /**
     * 获取所有的position信息
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/allpositions")
    public ResponseEntity<Map<String, Object>> getAllComments(
            @RequestParam(name = "pageNum",defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize",defaultValue = "10") Integer size
    ) {
        HashMap<String, Object> res = new HashMap<>();
        int offset = (page - 1) * size;
        System.out.println("size:" + size);
        System.out.println("offset:" + offset);
        System.out.println("page:" + page);
        List<JobPosting> positionList = jobImportService.getAllJobs(offset, size);
        res.put("data", positionList);
        int  count = jobImportService.getAllJobsCount();
        System.out.println("===================================");
        System.out.println("count:" + count);
        System.out.println("=====================================");
        res.put("total", count);
        String jsonStringWithDateFormat = JSON.toJSONStringWithDateFormat(res, "yyyy-MM-dd HH:mm:ss");
        return  ResponseEntity.ok(JSONObject.parseObject(jsonStringWithDateFormat));
    }

    /**
     * 爬虫接口
     */
    // 定义 Flask API 端点
    private final String FLASK_API_URL = "http://localhost:5000/start_crawler";

    // 启动爬虫脚本的接口
    @PostMapping("/startCrawler")
    public ResponseEntity<String> startCrawler(@RequestBody String requestData) {
        System.out.println("========================");
        System.out.println("请求到达后端，正在处理...");
        System.out.println("========================");
        System.out.println("收到的请求数据: " + requestData);
        System.out.println("==============================");

        try {
            // 将请求数据转化为 JSON 格式，假设请求数据是 {"auto": true}，可以通过解析成 Map 类型来处理
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("auto", true);  // 设置 auto 参数

            // 设置请求头，指定 Content-Type 为 application/json
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 使用 HttpEntity 构建请求实体，包含请求头和请求数据
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);

            // 使用 RestTemplate 发起 POST 请求，将 JSON 数据传递给 Flask 后端
            ResponseEntity<String> response = restTemplate.exchange(FLASK_API_URL, HttpMethod.POST, entity, String.class);

            // 判断请求是否成功
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("爬虫脚本启动成功！");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("启动爬虫脚本失败！");
            }
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            e.printStackTrace();
            return ResponseEntity.status(500).body("请求过程中发生错误：" + e.getMessage());
        }
    }

}