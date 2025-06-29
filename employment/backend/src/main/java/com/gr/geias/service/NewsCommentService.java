package com.gr.geias.service;

import com.gr.geias.dto.NewsCommentDTO;
import com.gr.geias.model.NewsComment;

import java.util.List;

public interface NewsCommentService {
    void addComment(NewsComment comment);
    List<NewsComment> getCommentsByNewsId(Integer newsId);
    List<NewsCommentDTO> getCommentsWithUserName(Integer newsId, int offset, int size);
    int deleteCommentById(Integer commentId);
    List<NewsCommentDTO> getAllComments(int offset, int size, boolean includeDeleted);
}
