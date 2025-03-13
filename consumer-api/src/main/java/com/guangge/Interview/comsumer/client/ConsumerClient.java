package com.guangge.Interview.comsumer.client;

import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.util.CommonResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = ConsumerConstant.SERVICE_NAME, url = "${consumer-service-endpoint}")
public interface ConsumerClient {
    @PostMapping(value = "/login")
    CommonResult<String> login(@RequestParam("name") String name,
                                      @RequestParam("code") String code);

    @PostMapping(value = "/auth/verify-token")
    CommonResult<String> verifyToken(@RequestHeader("token") String token);

    @GetMapping(value = "/frontend/interView")
    List<InterViewRecord> getInterView();

    @PostMapping(value = "/frontend/sendMail")
    void sendMail(@RequestParam("name") String name);

    @GetMapping(value = "/frontend/findInterView")
    List<InterViewRecord> findInterView(@RequestParam("question") String question);

    @PostMapping(value="/interview/face2faceChat", produces = "audio/wav")
    ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,
                                                @RequestParam("audio") MultipartFile audio);

    @GetMapping(value="/interview/welcomemp3", produces = "audio/mp3")
    ResponseEntity<byte[]> welcomemp3();

    @GetMapping(value = "/interview/makeProgram")
    ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                                 @RequestParam("name") String name);
}
