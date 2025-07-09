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
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import java.util.Base64;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;


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
     * @param userName 用户名
     * @param audio 面试者回答音频
     * @return 面试官问题音频
     */
    @PostMapping(value="/face2faceChat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "audio/wav")
    public ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,
                                               @RequestParam(value ="userName", required = false) String userName,
                                               @RequestPart(value = "audio", required = false) MultipartFile audio) {
        try {
            logger.info("Received face2face request at consumer: chatId={}, userName={}, audio={}", 
                      chatId, userName, (audio != null ? "present" : "null"));
                      
            String completed = "";
            String text = "";
            
            // 如果只有用户名，没有音频，则处理初始欢迎消息
            if (StringUtils.hasLength(userName) && audio == null) {
                // 明确指示大模型获取用户简历并开始面试
                text = "我是" + userName + "，请获取我的简历并开始面试";
                logger.info("Processing welcome message for user: {}, instructing AI to get resume", userName);
            } 
            // 如果有音频，处理用户语音输入
            else if (audio != null) {
                logger.info("Processing audio file: size={}, contentType={}", 
                           audio.getSize(), audio.getContentType());
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
            logger.info("Generated audio response: size={} bytes", audioResponse.length);

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
     * 面试接口 - 使用文件路径
     * @param chatId 回话id
     * @param userName 用户名
     * @param audioPath 音频文件路径
     * @return 面试官问题音频
     */
    @PostMapping(value="/face2faceChatWithPath", produces = "audio/wav")
    public ResponseEntity<byte[]> face2faceChatWithPath(@RequestParam("chatId") String chatId,
                                                      @RequestParam(value ="userName", required = false) String userName,
                                                      @RequestParam(value ="audioPath", required = false) String audioPath) {
        try {
            logger.info("Received face2face request with file path: chatId={}, userName={}, audioPath={}", 
                      chatId, userName, audioPath);

            // Process based on whether we have audio or not
            if (audioPath == null || audioPath.isEmpty()) {
                // No audio - just generate welcome message
                logger.info("No audio file path provided - generating welcome message");
                // 处理欢迎消息，修改为与其他方法一致的格式
                String text = "我是" + userName + "，请获取我的简历并开始面试";
                logger.info("Processing welcome message for user: {}, instructing AI to get resume", userName);
                
                // 获取大模型响应
                String response = interviewAssistant.chat(chatId, text);
                logger.info("AI response: {}", response);
                
                byte[] audioResponse = speechService.textToSpeech(response);
                return ResponseEntity.ok()
                        .header("Content-Type", "audio/wav")
                        .body(audioResponse);
            } else {
                // We have audio - process the interview
                File audioFile = new File(audioPath);
                if (!audioFile.exists()) {
                    logger.warn("Audio file not found at path: {}", audioPath);
                    return ResponseEntity.badRequest()
                        .body(("Audio file not found at: " + audioPath).getBytes());
                }
                
                logger.info("Reading audio file from path: {}, size: {} bytes", audioPath, audioFile.length());
                
                // 通过路径处理音频文件
                String text = "";
                try {
                    File convertFile = AudioConverter.convertToWav(audioFile);
                    text = speechToTextService.transcribeAudio(convertFile);
                    convertFile.delete();
                } catch (Exception e) {
                    logger.error("Error processing audio file: {}", e.getMessage());
                    text = "音频处理失败，请重新发送";
                }
                
                // 获取大模型响应
                String response = interviewAssistant.chat(chatId, text);
                logger.info("AI response: {}", response);
                
                // 文字转语音
                byte[] audioResponse = speechService.textToSpeech(response);
                
                return ResponseEntity.ok()
                        .header("Content-Type", "audio/wav")
                        .body(audioResponse);
            }
        } catch (Exception e) {
            logger.error("Error processing face2face chat: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error processing face2face chat: " + e.getMessage()).getBytes());
        }
    }

    /**
     * 面试接口 - 使用Base64编码的音频数据
     * @param chatId 回话id
     * @param userName 用户名
     * @param base64AudioData Base64编码的音频数据
     * @return 面试官问题音频
     */
    @PostMapping(value="/face2faceChatBytes", produces = "audio/wav")
    public byte[] face2faceChatBytes(@RequestParam("chatId") String chatId,
                                                  @RequestParam(value ="userName", required = false) String userName,
                                                  @RequestParam(value ="audioData", required = false) String base64AudioData) {
        try {
            logger.info("Received face2face request with base64 audio data: chatId={}, userName={}, audioDataLength={}", 
                      chatId, userName, (base64AudioData != null ? base64AudioData.length() : 0));

            // Process based on whether we have audio or not
            if (base64AudioData == null || base64AudioData.isEmpty()) {
                // No audio - just generate welcome message
                logger.info("No audio data provided - processing welcome message");
                // 处理欢迎消息，修改为与face2faceChat方法一致的格式
                String text = "我是" + userName + "，请获取我的简历并开始面试";
                logger.info("Processing welcome message for user: {}, instructing AI to get resume", userName);
                
                // 获取大模型响应
                String response = interviewAssistant.chat(chatId, text);
                logger.info("AI response: {}", response);
                
                return speechService.textToSpeech(response);
            } else {
                // We have audio - process the interview
                // 解码Base64音频数据
                byte[] audioBytes = Base64.getDecoder().decode(base64AudioData);
                logger.info("Decoded audio data, size: {} bytes", audioBytes.length);
                
                // 保存为临时文件
                File tempFile = File.createTempFile("audio-", ".wav");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(audioBytes);
                }
                
                // 处理音频文件
                String text = "";
                try {
                    File convertFile = AudioConverter.convertToWav(tempFile);
                    text = speechToTextService.transcribeAudio(convertFile);
                    convertFile.delete();
                    
                    // 处理语音识别结果为空的情况
                    if (text == null || text.trim().isEmpty()) {
                        logger.warn("Speech recognition returned empty text, using default message");
                        text = "我没有听清楚，请再说一遍";
                    }
                } catch (Exception e) {
                    logger.error("Error processing audio file: {}", e.getMessage());
                    text = "音频处理失败，请重新发送";
                } finally {
                    tempFile.delete();
                }
                
                // 获取大模型响应
                String response = interviewAssistant.chat(chatId, text);
                logger.info("AI response: {}", response);
                
                // 文字转语音
                return speechService.textToSpeech(response);
            }
        } catch (Exception e) {
            logger.error("Error processing face2face chat with bytes: {}", e.getMessage(), e);
            try {
                return ("Error processing face2face chat: " + e.getMessage()).getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return new byte[0];
            }
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

    /**
     * 测试文件上传接口
     * @param file 上传的文件
     * @return 文件信息
     */
    @PostMapping("/test-upload")
    public ResponseEntity<String> testUpload(@RequestPart("file") MultipartFile file) {
        logger.info("Received test upload request: filename={}, size={}, contentType={}", 
                  file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        StringBuilder info = new StringBuilder();
        info.append("File received successfully!\n");
        info.append("Filename: ").append(file.getOriginalFilename()).append("\n");
        info.append("Size: ").append(file.getSize()).append(" bytes\n");
        info.append("Content Type: ").append(file.getContentType()).append("\n");
        
        return ResponseEntity.ok(info.toString());
    }
}
