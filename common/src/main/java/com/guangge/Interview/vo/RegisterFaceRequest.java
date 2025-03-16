package com.guangge.Interview.vo;

public class RegisterFaceRequest {
    private String image; // Base64 编码的图片数据
    private String userId; // 用户ID

    // Getter 和 Setter 方法
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
