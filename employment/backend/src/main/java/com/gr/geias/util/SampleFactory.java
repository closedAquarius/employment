package com.gr.geias.util;

import com.baidu.aip.face.AipFace;

/**
 * 百度人脸识别API工厂类
 */
public class SampleFactory {
    // 设置APPID/AK/SK
    private static final String APP_ID = "你的APPID";
    private static final String API_KEY = "你的API_KEY";
    private static final String SECRET_KEY = "你的SECRET_KEY";
    
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