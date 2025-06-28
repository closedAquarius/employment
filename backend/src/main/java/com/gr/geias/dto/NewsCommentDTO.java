package com.gr.geias.dto;

import java.util.Date;

public class NewsCommentDTO {
    private Integer commentId;
    private Integer newsId;
    private Integer userId;
    private String userName;   // 来自 tb_person_info
    private String content;
    private Date createTime;
    private Integer isDeleted;
}
