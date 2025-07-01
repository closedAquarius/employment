package com.gr.geias.controller;

import com.gr.geias.dto.NewsCommentDTO;
import com.gr.geias.model.News;
import com.gr.geias.model.NewsComment;
import com.gr.geias.service.NewsCommentService;
import com.gr.geias.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsCommentService newsCommentService;

    @PostMapping("/uploadImages")
    public ResponseEntity<List<String>> uploadNewsImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body(Collections.singletonList("未上传文件"));
        }

        List<String> uploadedUrls = new ArrayList<>();

        // 获取项目启动目录
        String basePath = System.getProperty("user.dir");
        // 定义保存图片的文件夹
        String uploadDirPath = basePath + File.separator + "uploads" + File.separator + "news";

        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            boolean mkdirsResult = uploadDir.mkdirs();
            if (!mkdirsResult) {
                return ResponseEntity.status(500).body(Collections.singletonList("创建上传目录失败"));
            }
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 获取文件原始名
            String originalFilename = file.getOriginalFilename();
            // 取扩展名
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 使用UUID生成新的文件名，防止冲突
            String newFileName = UUID.randomUUID().toString() + extension;

            // 文件最终保存路径
            File destFile = new File(uploadDir, newFileName);

            try {
                // 保存文件到磁盘
                file.transferTo(destFile);

                // 这里拼接成前端访问的相对路径，比如 /uploads/news/xxx.jpg
                String urlPath = "/uploads/news/" + newFileName;
                uploadedUrls.add(urlPath);
            } catch (IOException e) {
                e.printStackTrace();
                // 上传失败，记录错误并跳过该文件
            }
        }

        if (uploadedUrls.isEmpty()) {
            return ResponseEntity.status(500).body(Collections.singletonList("上传失败"));
        } else {
            return ResponseEntity.ok(uploadedUrls);
        }
    }
    /**
     * 管理员可访问：发布一篇新闻
     */
    @PostMapping("/publish")
    public ResponseEntity<String> publishNews(@RequestBody News news) {
        news.setPublishTime(new Date());     // 设置当前时间为发布时间
        news.setUpdateTime(new Date());      // 设置当前时间为更新时间
        news.setReadCount(0);                // 初始阅读量为 0
        news.setIsDeleted(0);                // 默认未删除

        newsService.publishNews(news);
        return ResponseEntity.ok("发布成功");
    }

    /**
     * 所有人可访问：根据 news_id 获取新闻详情，并自动增加阅读量
     */
    @GetMapping("/{newsId}")
    public ResponseEntity<News> getNewsDetail(@PathVariable Integer newsId) {
        News news = newsService.getNewsById(newsId);
        if (news == null || news.getIsDeleted() == 1) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(news);
    }

    /**
     * 管理员可访问：更新单个新闻的信息
     */
    @PutMapping("/update")
    public ResponseEntity<String> updateNews(@RequestBody News news) {
        if (news.getNewsId() == null) {
            return ResponseEntity.badRequest().body("缺少newsId");
        }

        news.setUpdateTime(new Date()); // 更新更新时间
        int result = newsService.updateNews(news);
        if (result > 0) {
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.status(404).body("新闻不存在或已被删除");
        }
    }


    /**
     * 所有人可访问：分页获取新闻列表（门户展示）
     */
    @GetMapping("/list")
    public ResponseEntity<List<News>> getNewsList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String tags
    ) {
        int offset = (page - 1) * size;
        List<News> newsList = newsService.getNewsList(offset, size, tags);
        return ResponseEntity.ok(newsList);
    }

    /**
     * 所有人可访问：获取点击量最多的前N条新闻（默认5条）
     */
    @GetMapping("/hot")
    public ResponseEntity<List<News>> getHotNews(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<News> hotNews = newsService.getHotNews(limit);
        return ResponseEntity.ok(hotNews);
    }

    /**
     * 所有人可访问：获取最新发布的前N条新闻（默认5条）
     */
    @GetMapping("/latest")
    public ResponseEntity<List<News>> getLatestNews(
            @RequestParam(defaultValue = "5") Integer limit) {
        List<News> latestNews = newsService.getLatestNews(limit);
        return ResponseEntity.ok(latestNews);
    }

    /**
     * 所有人可访问：新闻模糊搜索（标题、正文、作者）
     */
    @GetMapping("/search")
    public ResponseEntity<List<News>> searchNews(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        int offset = (page - 1) * size;
        List<News> result = newsService.searchNews(keyword, offset, size);
        return ResponseEntity.ok(result);
    }


    /**
     * 登录用户可访问：对某新闻发表评论
     */
    @PostMapping("/{newsId}/comment")
    public ResponseEntity<String> postComment(
            @PathVariable Integer newsId,
            @RequestParam Integer userId,
            @RequestParam String content) {

        NewsComment comment = new NewsComment();
        comment.setNewsId(newsId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreateTime(new Date());
        comment.setIsDeleted(0);

        newsCommentService.addComment(comment);
        return ResponseEntity.ok("评论成功");
    }

    /**
     * 所有人可访问：获取某新闻的评论列表（按时间倒序）
     */
    @GetMapping("/{newsId}/comments")
    public ResponseEntity<List<NewsComment>> getComments(@PathVariable Integer newsId) {
        List<NewsComment> comments = newsCommentService.getCommentsByNewsId(newsId);
        return ResponseEntity.ok(comments);
    }

    /**
     * 所有人可访问：获取某新闻的评论列表（分页 + 显示评论人昵称）
     */
    @GetMapping("/{newsId}/commentsWithPerson")
    public ResponseEntity<List<NewsCommentDTO>> getCommentsByPage(
            @PathVariable Integer newsId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        int offset = (page - 1) * size;
        List<NewsCommentDTO> comments = newsCommentService.getCommentsWithUserName(newsId, offset, size);
        return ResponseEntity.ok(comments);
    }

    /**
     * 管理员可访问：逻辑删除新闻
     */
    @DeleteMapping("/{newsId}")
    public ResponseEntity<String> deleteNews(@PathVariable Integer newsId) {
        int result = newsService.deleteNewsById(newsId);
        if (result > 0) {
            return ResponseEntity.ok("新闻已删除");
        } else {
            return ResponseEntity.status(404).body("新闻不存在");
        }
    }

    /**
     * 管理员或本人可访问：逻辑删除评论
     */
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer commentId) {
        int result = newsCommentService.deleteCommentById(commentId);
        if (result > 0) {
            return ResponseEntity.ok("评论已删除");
        } else {
            return ResponseEntity.status(404).body("评论不存在");
        }
    }

    /**
     * 管理员可访问：分页获取所有新闻评论（可选显示逻辑删除的）
     * 包含已删除评论：GET /admin/comments?page=1&size=10&includeDeleted=true
     */
    @GetMapping("/admin/comments")
    public ResponseEntity<List<NewsCommentDTO>> getAllComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "false") Boolean includeDeleted
    ) {
        int offset = (page - 1) * size;
        List<NewsCommentDTO> commentList = newsCommentService.getAllComments(offset, size, includeDeleted);
        return ResponseEntity.ok(commentList);
    }
}
