package com.guangge.Interview.images;


import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

@Service
public class ImageGenerator {
    private final ImageModel imageModel;

    public ImageGenerator(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public ImageResponse generate(String prompt) {
        var options = ImageOptionsBuilder.builder()
                .width(1024)
                .height(1024)
                .build();

        var imagePrompt = new ImagePrompt(prompt, options);
        return imageModel.call(imagePrompt);
    }
}
