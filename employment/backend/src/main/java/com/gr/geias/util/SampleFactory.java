package com.gr.geias.util;

import com.baidu.aip.face.AipFace;

/**
 * 百度人脸识别API工厂类
 */
public class SampleFactory {
    // 设置APPID/AK/SK
    private static final String APP_ID = "119395855";
    private static final String API_KEY = "n76ryUKwPYaYAVC1GOA7Bo9x";
    private static final String SECRET_KEY = "bzEd6xW2EyGsBv4j7kSWfMwEXenrH2vH";
    
    private static AipFace aipFace;

    /**
     * 获取AipFace单例
     * @return AipFace实例
     */
    public static AipFace getInstance() {
        if (aipFace == null) {
            synchronized (SampleFactory.class) {
                if (aipFace == null) {
                    aipFace = new AipFace(APP_ID, API_KEY, SECRET_KEY);
                    // 设置网络连接参数
                    aipFace.setConnectionTimeoutInMillis(2000);
                    aipFace.setSocketTimeoutInMillis(60000);
                }
            }
        }
        return aipFace;
    }
} 