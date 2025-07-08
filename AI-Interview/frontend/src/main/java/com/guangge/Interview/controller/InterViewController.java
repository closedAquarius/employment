package com.guangge.Interview.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guangge.Interview.comsumer.client.ConsumerClient;
import com.guangge.Interview.record.ProgramRecord;
import com.guangge.Interview.services.ConsumerService;
import com.guangge.Interview.vo.ChatReqeust;
import com.guangge.Interview.vo.CheckProgramRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("interview")
public class InterViewController {
    private static final Logger logger = LoggerFactory.getLogger(InterViewController.class);
    private final ConsumerClient consumerClient;
    private final ConsumerService consumerService;
    private final WebClient webClient;

    @Value("${consumer-service-endpoint}")
    private String consumerServiceEndpoint;

    public InterViewController(ConsumerClient consumerClient, ConsumerService consumerService, WebClient.Builder webClientBuilder) {
        this.consumerClient = consumerClient;
        this.consumerService = consumerService;
        this.webClient = webClientBuilder.build();
    }

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestParam("chatId") String chatId, 
                             @RequestParam("userMessage") String userMessage,
                             @RequestHeader(value = "Authorization", required = false) String token) {
        return consumerService.interViewChat(chatId, userMessage, token);
    }

    @PostMapping(value = "/face2faceChat", produces = "audio/wav")
    public ResponseEntity<byte[]> face2faceChat(@RequestParam("chatId") String chatId,
                                               @RequestParam(value = "userName", required = false) String userName,
                                               @RequestParam(value = "userId", required = false) String userId,
                                               @RequestPart(value = "audio", required = false) MultipartFile audio,
                                               @RequestParam(value = "hasToken", required = false) String hasToken) {
        logger.info("Received face2face request: chatId={}, userName={}, userId={}, audio={}, hasToken={}", 
                  chatId, userName, userId, (audio != null ? "present" : "null"), hasToken);
        
            logger.info("Forwarding request to consumer service...");
            
        try {
            // For welcome message (no audio)
            if (audio == null) {
                try {
                    logger.info("No audio provided, requesting welcome message");
                    
                    // 使用WebClient直接调用consumer服务
                    byte[] audioData = webClient.post()
                        .uri(consumerServiceEndpoint + "/interview/face2faceChatBytes?chatId={chatId}&userName={userName}", 
                             chatId, userName)
                        .header("Content-Type", "application/json")
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block();
                    
                    return ResponseEntity.ok()
                        .header("Content-Type", "audio/wav")
                        .body(audioData);
                } catch (Exception e) {
                    logger.error("Error requesting welcome message: {}", e.getMessage(), e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Error requesting welcome message: " + e.getMessage()).getBytes());
                }
            }

            // For audio processing
            try {
                // Convert MultipartFile to Base64
            byte[] audioBytes = audio.getBytes();
            String base64Audio = Base64.getEncoder().encodeToString(audioBytes);
                logger.info("Converted audio to Base64, size: {} bytes", base64Audio.length());
            
                // 使用WebClient直接调用consumer服务
                Map<String, String> formData = new HashMap<>();
                formData.put("audioData", base64Audio);
                
                byte[] responseAudio = webClient.post()
                    .uri(consumerServiceEndpoint + "/interview/face2faceChatBytes?chatId={chatId}&userName={userName}", 
                         chatId, userName)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("audioData", base64Audio))
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
                
                return ResponseEntity.ok()
                    .header("Content-Type", "audio/wav")
                    .body(responseAudio);
            } catch (Exception e) {
                logger.error("Error processing audio: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error processing audio: " + e.getMessage()).getBytes());
            }
        } catch (Exception e) {
            logger.error("Unexpected error in face2faceChat: {}", e.getMessage(), e);
            try {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Unexpected error: " + e.getMessage()).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new byte[0]);
            }
        }
    }

    @GetMapping(value="/welcomemp3", produces = "audio/mp3")
    public ResponseEntity<byte[]> welcomemp3() {
        return this.consumerClient.welcomemp3();
    }

    @GetMapping(value = "/makeProgram")
    public ResponseEntity<ProgramRecord> program(@RequestParam("first") Boolean first,
                                                 @RequestParam("name") String name) {
        return this.consumerClient.program(first, name);
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = "/checkProgram", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> checkProgram(@RequestParam("data") String encodedData) throws JsonProcessingException, UnsupportedEncodingException {
        // 1. 解码 Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encodedData);
        String urlEncodedData = new String(decodedBytes, StandardCharsets.UTF_8);

        // 2. 解码 URL 编码
        String jsonParams = URLDecoder.decode(urlEncodedData, StandardCharsets.UTF_8.toString());

        // 3. 将 JSON 字符串转换为对象
        CheckProgramRequest request = objectMapper.readValue(jsonParams, CheckProgramRequest.class);

        logger.debug("Question: " + request.getQuestion());
        logger.debug("Input: " + request.getInput());
        logger.debug("Output: " + request.getOutput());
        logger.debug("Code: " + request.getCode());

        return this.consumerService.checkProgram(request);
    }
    
    @PostMapping(value = "/checkProgram", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> checkProgramPost(@RequestBody CheckProgramRequest request) {
        logger.debug("POST Question: " + request.getQuestion());
        logger.debug("POST Input: " + request.getInput());
        logger.debug("POST Output: " + request.getOutput());
        logger.debug("POST Code: " + request.getCode());
        
        return this.consumerService.checkProgram(request);
    }

    /**
     * 测试文件上传接口
     * @param file 上传的文件
     * @return 文件信息
     */
    @PostMapping("/test-upload")
    public ResponseEntity<String> testUpload(@RequestPart("file") MultipartFile file) {
        logger.info("Received test upload request at frontend: filename={}, size={}, contentType={}", 
                  file.getOriginalFilename(), file.getSize(), file.getContentType());
        
        StringBuilder info = new StringBuilder();
        info.append("File received successfully at frontend!\n");
        info.append("Filename: ").append(file.getOriginalFilename()).append("\n");
        info.append("Size: ").append(file.getSize()).append(" bytes\n");
        info.append("Content Type: ").append(file.getContentType()).append("\n");
        
        return ResponseEntity.ok(info.toString());
    }

    /**
     * 测试音频处理接口 - 不使用Feign客户端
     * @param chatId 回话ID
     * @param userName 用户名
     * @return 测试音频
     */
    @PostMapping(value="/test-audio", produces = "audio/wav")
    public ResponseEntity<byte[]> testAudio(@RequestParam("chatId") String chatId,
                                       @RequestParam(value ="userName", required = false) String userName) {
        logger.info("Received test audio request: chatId={}, userName={}", chatId, userName);
        
        try {
            // Generate a simple sine wave as test audio
            int sampleRate = 44100;
            double frequency = 440; // A4 note
            double amplitude = 0.5;
            double seconds = 2.0;
            
            // Create WAV header
            byte[] header = new byte[44];
            // "RIFF" chunk descriptor
            header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
            // Chunk size (file size - 8)
            int fileSize = (int)(44 + sampleRate * seconds * 2) - 8;
            header[4] = (byte)(fileSize & 0xff);
            header[5] = (byte)((fileSize >> 8) & 0xff);
            header[6] = (byte)((fileSize >> 16) & 0xff);
            header[7] = (byte)((fileSize >> 24) & 0xff);
            // Format ("WAVE")
            header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
            // "fmt " subchunk
            header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
            // Subchunk size (16 for PCM)
            header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0;
            // Audio format (1 for PCM)
            header[20] = 1; header[21] = 0;
            // Number of channels (1 for mono)
            header[22] = 1; header[23] = 0;
            // Sample rate
            header[24] = (byte)(sampleRate & 0xff);
            header[25] = (byte)((sampleRate >> 8) & 0xff);
            header[26] = (byte)((sampleRate >> 16) & 0xff);
            header[27] = (byte)((sampleRate >> 24) & 0xff);
            // Byte rate (SampleRate * NumChannels * BitsPerSample/8)
            int byteRate = sampleRate * 1 * 16 / 8;
            header[28] = (byte)(byteRate & 0xff);
            header[29] = (byte)((byteRate >> 8) & 0xff);
            header[30] = (byte)((byteRate >> 16) & 0xff);
            header[31] = (byte)((byteRate >> 24) & 0xff);
            // Block align (NumChannels * BitsPerSample/8)
            header[32] = 2; header[33] = 0;
            // Bits per sample
            header[34] = 16; header[35] = 0;
            // "data" subchunk
            header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
            // Subchunk size (NumSamples * NumChannels * BitsPerSample/8)
            int dataSize = (int)(sampleRate * seconds * 2);
            header[40] = (byte)(dataSize & 0xff);
            header[41] = (byte)((dataSize >> 8) & 0xff);
            header[42] = (byte)((dataSize >> 16) & 0xff);
            header[43] = (byte)((dataSize >> 24) & 0xff);
            
            // Create audio data
            int numSamples = (int)(sampleRate * seconds);
            byte[] audioData = new byte[numSamples * 2]; // 16-bit samples
            
            for (int i = 0; i < numSamples; i++) {
                double time = i / (double)sampleRate;
                double value = amplitude * Math.sin(2 * Math.PI * frequency * time);
                // Convert to 16-bit PCM
                short sample = (short)(value * 32767);
                audioData[i*2] = (byte)(sample & 0xff);
                audioData[i*2 + 1] = (byte)((sample >> 8) & 0xff);
            }
            
            // Combine header and audio data
            byte[] wavFile = new byte[header.length + audioData.length];
            System.arraycopy(header, 0, wavFile, 0, header.length);
            System.arraycopy(audioData, 0, wavFile, header.length, audioData.length);
            
            logger.info("Generated test audio: {} bytes", wavFile.length);
            
            return ResponseEntity.ok()
                .header("Content-Type", "audio/wav")
                .body(wavFile);
            
        } catch (Exception e) {
            logger.error("Error generating test audio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(("Error generating test audio: " + e.getMessage()).getBytes());
        }
    }
}
