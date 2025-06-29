package com.guangge.Interview.audio;

import com.guangge.Interview.audio.services.AudioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioController {
    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @GetMapping(value = "/talk")
    public String talktalk(@RequestParam String title,@RequestParam String question) {
        return  audioService.getSpeech(title,question);
    }
}
