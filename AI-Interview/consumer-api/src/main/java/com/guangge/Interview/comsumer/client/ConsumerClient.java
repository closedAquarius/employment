package com.guangge.Interview.comsumer.client;

import com.guangge.Interview.record.CandidateRecord;
import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.CvRequest;
import com.guangge.Interview.vo.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@FeignClient(name = ConsumerConstant.SERVICE_NAME, url = "${consumer-service-endpoint}", configuration = FeignConfig.class)
public interface ConsumerClient {
    @PostMapping(value = "/interview/login")
    CommonResult<UserResponse> login(@RequestBody Map<String, String> loginInfo);

    @PostMapping(value = "/auth/verify-token")
    CommonResult<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token);

    @GetMapping(value = "/frontend/interView")
    List<InterViewRecord> getInterView();

    @GetMapping(value = "/frontend/candidates")
    List<CandidateRecord> getCandidates();

    @PostMapping(value = "/frontend/sendMail")
    void sendMail(@RequestParam("name") String name, 
                 @RequestParam(value = "email", required = false) String email);

    @GetMapping(value = "/frontend/findInterView")
    List<InterViewRecord> findInterView(@RequestParam("question") String question);

    @PostMapping(value="/interview/face2faceChat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId, @RequestParam(value ="userName", required = false) String userName,
                                                @RequestParam(value = "audio", required = false) MultipartFile audio);

    @GetMapping(value="/interview/welcomemp3", produces = "audio/mp3")
    ResponseEntity<byte[]> welcomemp3();

    @GetMapping(value = "/interview/makeProgram")
    ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                                 @RequestParam("name") String name);

    @PostMapping(value = "/resume/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CommonResult<String> uploadResume(@RequestPart("resume") MultipartFile resumeFile,
                                             @RequestParam("jd") String jdText);

    @PostMapping(value = "/resume/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    CommonResult<String> createCv(@RequestBody CvRequest cvRequest);
}
