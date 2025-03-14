package com.guangge.Interview.comsumer.client;

import com.guangge.Interview.record.InterViewRecord;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.util.CommonResult;
import com.guangge.Interview.vo.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = ConsumerConstant.SERVICE_NAME, url = "${consumer-service-endpoint}", configuration = FeignConfig.class)
public interface ConsumerClient {
    @PostMapping(value = "/login")
    CommonResult<UserResponse> login(@RequestParam("name") String name,
                                     @RequestParam("code") String code);

    @PostMapping(value = "/auth/verify-token")
    CommonResult<String> verifyToken(@RequestHeader("token") String token);

    @GetMapping(value = "/frontend/interView")
    List<InterViewRecord> getInterView();

    @PostMapping(value = "/frontend/sendMail")
    void sendMail(@RequestParam("name") String name);

    @GetMapping(value = "/frontend/findInterView")
    List<InterViewRecord> findInterView(@RequestParam("question") String question);

    @PostMapping(value="/interview/face2faceChat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,@RequestParam(value ="userName", required = false) String userName,
                                                @RequestPart(value = "audio", required = false) MultipartFile audio);

    @GetMapping(value="/interview/welcomemp3", produces = "audio/mp3")
    ResponseEntity<byte[]> welcomemp3();

    @GetMapping(value = "/interview/makeProgram")
    ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                                 @RequestParam("name") String name);
}
