package com.guangge.Interview.controller;

import com.guangge.Interview.assistant.InterviewAssistant;
import com.guangge.Interview.assistant.JavaAssistant;
import com.guangge.Interview.assistant.ProgramAssistant;
import com.guangge.Interview.audio.services.AudioConverter;
import com.guangge.Interview.audio.services.SpeechToTextService;
import com.guangge.Interview.audio.services.TextToSpeechService;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.util.JacksonMapperUtils;
import com.guangge.Interview.vo.ChatReqeust;
import com.guangge.Interview.vo.CheckProgramRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("interview")
public class InterViewController {
    private static final Logger logger = LoggerFactory.getLogger(InterViewController.class);

    private final JavaAssistant interViewAgent;
    private final InterviewAssistant interviewAssistant;
    private final TextToSpeechService speechService;
    private final SpeechToTextService speechToTextService;
    private final ResourceLoader resourceLoader;
    private final ProgramAssistant programAssistant;

    public InterViewController(JavaAssistant interViewAgent, InterviewAssistant interviewAssistant,
                               TextToSpeechService speechService, SpeechToTextService speechToTextService,
                               ResourceLoader resourceLoader, ProgramAssistant programAssistant) {
        this.interViewAgent = interViewAgent;
        this.interviewAssistant = interviewAssistant;
        this.speechService = speechService;
        this.speechToTextService = speechToTextService;
        this.resourceLoader = resourceLoader;
        this.programAssistant = programAssistant;
    }


    /**
     * 笔试题面试接口
     * @param chatReqeust 回话内容
     * @return 面试官回答内容
     */
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody ChatReqeust chatReqeust)  {
        return interViewAgent.chat(chatReqeust.getChatId(), chatReqeust.getUserMessage());
    }

    /**
     * 面试接口
     * @param chatId 回话id
     * @param audio 面试者回答音频
     * @return 面试官问题音频
     * @throws IOException
     * @throws InterruptedException
     * @throws UnsupportedAudioFileException
     */
    @PostMapping(value="/face2faceChat", produces = "audio/wav")
    public ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,
                                               @RequestParam(value ="userName", required = false) String userName,
                                               @RequestPart(value = "audio", required = false) MultipartFile audio) {
        try {
            logger.info("Received face2face request: chatId={}, userName={}, audio={}", 
                      chatId, userName, (audio != null ? "present" : "null"));
                      
            String completed = "";
            String text = "";
            
            // 如果只有用户名，没有音频，则处理初始欢迎消息
            if (StringUtils.hasLength(userName) && audio == null) {
                text = userName;
                logger.info("Processing welcome message for user: {}", userName);
            } 
            // 如果有音频，处理用户语音输入
            else if (audio != null) {
                File tempFile = File.createTempFile("audio-", ".opus");
                audio.transferTo(tempFile);
                File convertFile = AudioConverter.convertToWav(tempFile);
                // 语音转文字
                text = speechToTextService.transcribeAudio(convertFile);
                logger.info("Transcribed audio to text: {}", text);
                
                // 如果语音识别结果为空，发送默认消息
                if (text == null || text.trim().isEmpty()) {
                    text = "我没有听清楚，请再说一遍";
                    logger.info("Speech recognition result is empty, using default message: {}", text);
                }
                
                tempFile.delete();
                convertFile.delete();
            } else {
                logger.error("Invalid request: neither userName nor audio provided");
                return ResponseEntity.badRequest().build();
            }

            // 获取大模型响应
            String response = interviewAssistant.chat(chatId, text);
            logger.info("AI response: {}", response);

            if (response.contains("再见")) {
                completed = "completed";
            }

            // 文字转语音
            byte[] audioResponse = speechService.textToSpeech(response);

            return ResponseEntity.ok()
                    .header("Content-Type", "audio/wav")
                    .header("X-Chat-Status", completed) // 状态信息
                    .body(audioResponse);
        } catch (Exception e) {
            logger.error("Error in face2faceChat: ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 欢迎音频取得
     * @return 欢迎音频
     * @throws IOException
     */
    @GetMapping(value="/welcomemp3", produces = "audio/mp3")
    public ResponseEntity<byte[]> welcomemp3() throws IOException {
        Resource resource = new ClassPathResource("static/audio/welcome.mp3");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        byte[] audioBytes = Files.readAllBytes(resource.getFile().toPath());
        return ResponseEntity.ok()
                .header("Content-Type", "audio/mp3")
                .body(audioBytes);
    }

    @GetMapping(value = "/makeProgram")
    public ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                                 @RequestParam("name") String name) throws Exception {
        String result = this.programAssistant.makeQuestion(first,name);
        return ResponseEntity.ok(JacksonMapperUtils.json2pojo(result,ProgramRecord.class));
    }

    /**
     * 检查结果
     * @param request 信息
     * @return 结果
     * @throws Exception
     */
    @PostMapping(value = "/checkProgram", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> checkProgram(@RequestBody CheckProgramRequest request) throws Exception {
        String userContent = """
                                题目：{question},
                                示例输入:{input}
                                示例输出:{output}
                                回答内容:{code}
                                """;
        Flux<String> result = this.programAssistant.reviewQuestion(request.getQuestion(),request.getInput(),request.getOutput(),request.getCode());
        return result;
    }
}
