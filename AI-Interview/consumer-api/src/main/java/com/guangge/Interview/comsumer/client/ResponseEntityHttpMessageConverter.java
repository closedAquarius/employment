package com.guangge.Interview.comsumer.client;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 自定义HttpMessageConverter，用于处理ResponseEntity<byte[]>类型的响应
 */
public class ResponseEntityHttpMessageConverter implements HttpMessageConverter<ResponseEntity<byte[]>> {

    private final ByteArrayHttpMessageConverter byteArrayConverter;

    public ResponseEntityHttpMessageConverter() {
        this.byteArrayConverter = new ByteArrayHttpMessageConverter();
        List<MediaType> mediaTypes = Arrays.asList(
                MediaType.APPLICATION_OCTET_STREAM,
                MediaType.valueOf("audio/wav"),
                MediaType.valueOf("audio/mp3"),
                MediaType.ALL
        );
        this.byteArrayConverter.setSupportedMediaTypes(mediaTypes);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return ResponseEntity.class.isAssignableFrom(clazz) && 
               (mediaType == null || 
                mediaType.includes(MediaType.APPLICATION_OCTET_STREAM) || 
                "audio".equals(mediaType.getType()));
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false; // 我们只需要读取功能，不需要写入
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.ALL);
    }

    @Override
    public ResponseEntity<byte[]> read(Class<? extends ResponseEntity<byte[]>> clazz, HttpInputMessage inputMessage) 
            throws IOException, HttpMessageNotReadableException {
        byte[] body = byteArrayConverter.read(byte[].class, inputMessage);
        return ResponseEntity.ok()
                .contentType(inputMessage.getHeaders().getContentType())
                .body(body);
    }

    @Override
    public void write(ResponseEntity<byte[]> responseEntity, MediaType contentType, HttpOutputMessage outputMessage) 
            throws IOException, HttpMessageNotWritableException {
        // 不需要实现，因为我们只需要读取功能
        throw new UnsupportedOperationException("Writing is not supported");
    }
} 