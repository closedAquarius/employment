package com.guangge.Interview.images;

import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {
    private final ImageGenerator imageGenerator;

    @Autowired
    public ImageController(ImageGenerator imageGenerator) {
        this.imageGenerator = imageGenerator;
    }

    @GetMapping("/image")
    public ImageResponse generateImage(@RequestParam String prompt) {
        return imageGenerator.generate(prompt);
    }
}
