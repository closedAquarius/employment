package com.gr.geias.repository;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.PrivateMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PrivateMessageRepository {
    int insertMessage(PrivateMessage message);

    List<Integer> selectChatContacts(Integer userId);

    List<PersonInfo> selectChatContactsDetails(@Param("userId") Integer userId);

    /**
     * 查询两个用户之间的所有聊天消息（含双向），按发送时间升序
     */
    List<PrivateMessage> selectChatMessagesBetweenUsers(
            @Param("userId") Integer userId,
            @Param("otherId") Integer otherId);

    void updateMessagesRead(@Param("userId") Integer userId, @Param("otherId") Integer otherId);

    PrivateMessage selectById(Long messageId);

    void updateDeletedSender(@Param("messageId") Long messageId, @Param("deleted") boolean deleted);

    void updateDeletedReceiver(@Param("messageId") Long messageId, @Param("deleted") boolean deleted);

    void deleteById(Long messageId);
}
