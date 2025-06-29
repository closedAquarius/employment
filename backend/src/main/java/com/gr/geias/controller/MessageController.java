package com.gr.geias.controller;

import com.gr.geias.model.PersonInfo;
import com.gr.geias.model.PrivateMessage;
import com.gr.geias.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private PrivateMessageService privateMessageService;

    /**
     * 发送私聊消息
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody PrivateMessage message) {
        if (message.getSenderId() == null || message.getReceiverId() == null || message.getContent() == null) {
            return ResponseEntity.badRequest().body("参数缺失");
        }

        message.setSendTime(new Date());
        message.setIsRead(0);
        message.setIsDeletedSender(0);
        message.setIsDeletedReceiver(0);
        // 检查字段是否合理
        if (message.getMessageType() == null) {
            message.setMessageType(0); // 默认文本
        }

        int result = privateMessageService.sendMessage(message);
        if (result > 0) {
            return ResponseEntity.ok("发送成功");
        } else {
            return ResponseEntity.status(500).body("发送失败");
        }
    }

    /**
     * 获得所有与该人聊过天的用户列表
     */
    @GetMapping("/contacts/{userId}")
/*    public ResponseEntity<List<Integer>> getChatContacts(@PathVariable Integer userId) {
        List<Integer> contacts = privateMessageService.getChatContacts(userId);
        return ResponseEntity.ok(contacts);
    }*/
    public ResponseEntity<List<PersonInfo>> getChatContacts(@PathVariable Integer userId) {
        List<PersonInfo> contacts = privateMessageService.getChatContactsDetails(userId);
        return ResponseEntity.ok(contacts);
    }

    /**
     * 根据用户ID和聊天对方ID，查询两人间的聊天消息列表（按发送时间升序）
     * @param userId 当前登录用户ID
     * @param otherId 聊天对象用户ID
     * @return 消息列表
     */
    @GetMapping("/chatHistory")
    public ResponseEntity<List<PrivateMessage>> getChatHistory(
            @RequestParam Integer userId,
            @RequestParam Integer otherId) {
        // 先标记未读消息为已读
        privateMessageService.markMessagesRead(userId, otherId);

        // 再返回聊天记录
        List<PrivateMessage> messages = privateMessageService.getChatMessages(userId, otherId);
        return ResponseEntity.ok(messages);
    }

    /**
     * 用户删除私聊消息（逻辑删除）
     * @param userId 当前操作用户ID
     * @param messageId 要删除的消息ID
     */
    @DeleteMapping("/deleteMessage")
    public ResponseEntity<String> deleteMessage(
            @RequestParam Integer userId,
            @RequestParam Long messageId) {
        boolean success = privateMessageService.deleteMessage(userId, messageId);
        if (success) {
            return ResponseEntity.ok("删除成功");
        } else {
            return ResponseEntity.status(403).body("无权限删除该消息或消息不存在");
        }
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<String> uploadFile(@RequestParam Integer userId,
                                             @RequestParam Integer receiverId,
                                             @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传文件为空");
        }

        String basePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "messages";
        File dir = new File(basePath);
        if (!dir.exists()) dir.mkdirs();

        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String newName = UUID.randomUUID().toString() + ext;
        File dest = new File(dir, newName);
        try {
            file.transferTo(dest);
            String url = "/uploads/messages/" + newName;  // 前端可访问
            privateMessageService.sendMessageIfFile(userId, receiverId,originalName, url);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("上传失败");
        }
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<String> uploadImage(@RequestParam Integer userId,
                                             @RequestParam Integer receiverId,
                                             @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传文件为空");
        }

        String basePath = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "messages";
        File dir = new File(basePath);
        if (!dir.exists()) dir.mkdirs();

        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf("."));
        String newName = UUID.randomUUID().toString() + ext;
        File dest = new File(dir, newName);
        try {
            file.transferTo(dest);
            String url = "/uploads/messages/" + newName;  // 前端可访问
            privateMessageService.sendMessageIfImage(userId, receiverId,originalName, url);
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("上传失败");
        }
    }
}
