package com.gr.geias.service.impl;

import com.gr.geias.dto.NewsCommentDTO;
import com.gr.geias.model.NewsComment;
import com.gr.geias.repository.NewsCommentRepository;
import com.gr.geias.service.NewsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsCommentServiceImpl implements NewsCommentService {
    @Autowired
    private NewsCommentRepository newsCommentRepository;

    @Override
    public void addComment(NewsComment comment) {
        newsCommentRepository.insertComment(comment);
    }

    @Override
    public List<NewsComment> getCommentsByNewsId(Integer newsId) {
        return newsCommentRepository.selectCommentsByNewsId(newsId);
    }

    @Override
    public List<NewsCommentDTO> getCommentsWithUserName(Integer newsId, int offset, int size) {
        return newsCommentRepository.selectCommentsWithUserName(newsId, offset, size);
    }

    @Override
    public int deleteCommentById(Integer commentId) {
        return newsCommentRepository.logicDeleteComment(commentId);
    }

    @Override
    public List<NewsCommentDTO> getAllComments(int offset, int size, boolean includeDeleted) {
        return newsCommentRepository.selectAllComments(offset, size, includeDeleted);
    }
}
