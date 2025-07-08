package com.guangge.Interview.audio.services;


import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@RequiredArgsConstructor
@Service
public class AudioService {

    @Value("${spring.ai.dash-scope.audio.api-key}")
    private String apiKey;

    @Value("${spring.ai.dash-scope.audio.options.model}")
    private String model;

    public String getSpeech(String title,String question) {
        SpeechSynthesizer synthesizer = new SpeechSynthesizer();
        SpeechSynthesisParam param =
                SpeechSynthesisParam.builder()
                        .apiKey(apiKey)
                        .model(model)
                        .text(question)
                        .sampleRate(48000)
                        .build();

        // 确保audio目录存在
        File audioDir = new File("audio");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
            System.out.println("创建audio目录: " + audioDir.getAbsolutePath());
        }

        String name = "audio/" + title + ".mp3";
        File file = new File(name);
        if (file.exists()) {
            file.delete();
        }
        // 调用call方法，传入param参数，获取合成音频
        ByteBuffer audio = synthesizer.call(param);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audio.array());
            System.out.println("synthesis done!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file.getAbsolutePath();
    }
}
