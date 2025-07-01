package com.guangge.Interview.audio.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class AudioConverter {

    public static File convertToWav(File inputFile) throws IOException, InterruptedException {
        // 获取 FFmpeg 路径
        String ffmpegPath = getFFmpegPath();
        
        System.out.println("Using FFmpeg path: " + ffmpegPath); // 添加调试日志

        // 创建临时文件用于存储转换后的音频
        File outputFile = File.createTempFile("converted-", ".wav");
        outputFile.deleteOnExit();

        // 如果输出文件已存在，删除它
        if (outputFile.exists()) {
            outputFile.delete();
        }

        // 构建 FFmpeg 命令
        ProcessBuilder processBuilder = new ProcessBuilder(
                ffmpegPath,
                "-y", // 强制覆盖输出文件
                "-i", inputFile.getAbsolutePath(),
                "-ar", "16000",
                "-ac", "1",
                "-sample_fmt", "s16",
                "-fflags", "+genpts",      // 修复时间戳
                "-avoid_negative_ts", "make_zero",
                outputFile.getAbsolutePath()
        );

        // 启动进程
        Process process = processBuilder.start();

        // 使用多线程读取输出流和错误流
        Thread outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("FFmpeg output: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        outputThread.start();

        Thread errorThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println("FFmpeg error: " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        errorThread.start();

        // 设置超时时间并等待进程完成
        boolean finished = process.waitFor(10, TimeUnit.SECONDS);
        if (!finished) {
            process.destroy(); // 强制结束进程
            throw new IOException("FFmpeg 处理超时");
        }

        // 等待线程结束
        outputThread.join();
        errorThread.join();

        // 检查退出码
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new IOException("FFmpeg 转换失败，退出码: " + exitCode);
        }

        return outputFile;
    }

    private static String getFFmpegPath() {
        String os = System.getProperty("os.name").toLowerCase();
        
        // 优先使用系统安装的ffmpeg
        if (os.contains("mac") || os.contains("darwin") || os.contains("linux")) {
            // 在macOS或Linux上，尝试使用系统安装的ffmpeg
            try {
                Process process = new ProcessBuilder("which", "ffmpeg").start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String path = reader.readLine();
                    if (path != null && !path.isEmpty()) {
                        return path;
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to find system ffmpeg: " + e.getMessage());
            }
        }
        
        // 如果系统没有安装ffmpeg，回退到项目内置的ffmpeg
        String basePath = "/Users/lqy0584/Downloads/project/employment/AI-Interview";
        
        if (os.contains("win")) {
            // Windows 路径
            return basePath + "/ffmpeg-7.1.1-essentials_build/bin/ffmpeg.exe";
        } else if (os.contains("mac") || os.contains("darwin")) {
            // macOS 路径，使用硬编码的路径
            return "/opt/homebrew/bin/ffmpeg";
        } else if (os.contains("linux")) {
            // Linux 路径
            return basePath + "/ffmpeg-7.1.1-essentials_build/bin/ffmpeg";
        } else {
            throw new UnsupportedOperationException("Unsupported operating system: " + os);
        }
    }
    
    /**
     * 获取项目根目录路径 - 不再使用此方法，但保留作为备份
     */
    private static String getProjectRootPath() {
        try {
            // 获取当前类的路径
            String currentPath = AudioConverter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            
            // 解码URL编码
            currentPath = java.net.URLDecoder.decode(currentPath, "UTF-8");
            
            System.out.println("Current path: " + currentPath); // 调试信息
            
            // 检查路径是否包含target，如果是则返回其父目录的父目录
            if (currentPath.contains("/target/")) {
                Path path = Paths.get(currentPath).getParent().getParent().getParent();
                String result = path.toString();
                System.out.println("Computed path: " + result); // 调试信息
                return result;
            }
            
            // 如果不包含target，尝试获取用户目录
            return System.getProperty("user.dir");
        } catch (Exception e) {
            System.err.println("Failed to get project root path: " + e.getMessage());
            // 回退到用户目录
            return System.getProperty("user.dir");
        }
    }
}