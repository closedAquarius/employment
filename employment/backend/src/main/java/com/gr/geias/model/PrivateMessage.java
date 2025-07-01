package com.gr.geias.model;

import lombok.Data;

import java.util.Date;

@Data
public class PrivateMessage {
    private Long messageId;         // 消息ID
    private Integer senderId;       // 发送者ID
    private Integer receiverId;     // 接收者ID
    private String content;         // 消息内容
    private Date sendTime;          // 发送时间
    private Integer isRead;         // 是否已读（0=未读，1=已读）
    private Integer isDeletedSender;    // 发送方是否删除
    private Integer isDeletedReceiver;  // 接收方是否删除
    private Integer messageType;  // 0=文本，1=图片，2=文件
    private String fileUrl;
}
