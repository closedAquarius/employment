package com.gr.geias.model;

import lombok.Data;

import java.util.Date;

@Data
public class News {
    private Integer newsId;         // 新闻ID
    private String title;           // 新闻标题
    private String content;         // 正文内容
    private String imageUrls;       // 图片URL（用 | 分隔）
    private String authorName;      // 文章作者
    private Integer publisherId;    // 发布者ID
    private Date publishTime;       // 发布时间
    private Date updateTime;        // 更新时间
    private Integer readCount;      // 阅读量
    private String tags;            // 标签
    private Integer isDeleted;      // 是否逻辑删除（0=正常，1=已删除）
}
