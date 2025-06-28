package com.gr.geias.model;

import lombok.Data;

import java.util.Date;

@Data
public class NewsComment {
    private Integer commentId;
    private Integer newsId;
    private Integer userId;
    private String content;
    private Date createTime;
    private Integer isDeleted;

    // Getter & Setter 略
}
