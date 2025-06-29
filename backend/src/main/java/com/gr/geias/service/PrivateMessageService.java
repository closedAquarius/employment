package com.gr.geias.service;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.PrivateMessage;

import java.util.List;

public interface PrivateMessageService {
    int sendMessage(PrivateMessage message);

    List<Integer> getChatContacts(Integer userId);

    /**
     * 获取所有与该用户聊过天的联系人详细信息列表
     */
    List<PersonInfo> getChatContactsDetails(Integer userId);

    /**
     * 查询两个用户之间的所有聊天消息，按发送时间排序
     */
    List<PrivateMessage> getChatMessages(Integer userId, Integer otherId);

    void markMessagesRead(Integer userId, Integer otherId);

    boolean deleteMessage(Integer userId, Long messageId);

    int sendMessageIfFile(Integer senderId, Integer receiverId, String content,String contentWithFileUrl);

    int sendMessageIfImage(Integer senderId, Integer receiverId, String content,String contentWithFileUrl);
}
