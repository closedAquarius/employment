package com.gr.geias.service.impl;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.PrivateMessage;
import com.gr.geias.repository.PrivateMessageRepository;
import com.gr.geias.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    @Autowired
    private PrivateMessageRepository privateMessageMapper;

    @Override
    public int sendMessage(PrivateMessage message) {
        return privateMessageMapper.insertMessage(message);
    }

    @Override
    public List<Integer> getChatContacts(Integer userId) {
        return privateMessageMapper.selectChatContacts(userId);
    }

    @Override
    public List<PersonInfo> getChatContactsDetails(Integer userId) {
        return privateMessageMapper.selectChatContactsDetails(userId);
    }

    @Override
    public List<PrivateMessage> getChatMessages(Integer userId, Integer otherId) {
        return privateMessageMapper.selectChatMessagesBetweenUsers(userId, otherId);
    }

    @Override
    public void markMessagesRead(Integer userId, Integer otherId) {
        privateMessageMapper.updateMessagesRead(userId, otherId);
    }

    @Override
    public boolean deleteMessage(Integer userId, Long messageId) {
        // 先查消息，确认用户是发送方或接收方
        PrivateMessage message = privateMessageMapper.selectById(messageId);
        if (message == null) {
            return false;
        }
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            return false; // 无权限删除
        }
        if (message.getSenderId().equals(userId)) {
            // 标记发送方已删除
            privateMessageMapper.updateDeletedSender(messageId, true);
        }
        if (message.getReceiverId().equals(userId)) {
            // 标记接收方已删除
            privateMessageMapper.updateDeletedReceiver(messageId, true);
        }
        // 如果发送方和接收方都删除了，可以物理删除消息（选做）
        PrivateMessage updatedMsg = privateMessageMapper.selectById(messageId);
        if (updatedMsg.getIsDeletedSender() == 1 && updatedMsg.getIsDeletedReceiver() == 1) {
            privateMessageMapper.deleteById(messageId);
        }
        return true;
    }

    public int sendMessageIfFile(Integer senderId, Integer receiverId, String content,String contentWithFileUrl) {
        PrivateMessage msg = new PrivateMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setSendTime(new Date());
        msg.setIsRead(0);
        msg.setIsDeletedSender(0);
        msg.setIsDeletedReceiver(0);
        msg.setMessageType(2);  // 这是文件类型
        msg.setFileUrl(contentWithFileUrl);
        return privateMessageMapper.insertMessage(msg);
    }

    public int sendMessageIfImage(Integer senderId, Integer receiverId, String content,String contentWithFileUrl) {
        PrivateMessage msg = new PrivateMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setSendTime(new Date());
        msg.setIsRead(0);
        msg.setIsDeletedSender(0);
        msg.setIsDeletedReceiver(0);
        msg.setMessageType(1);  // 这是图片类型
        msg.setFileUrl(contentWithFileUrl);
        return privateMessageMapper.insertMessage(msg);
    }
}
