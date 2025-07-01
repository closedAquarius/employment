package com.gr.geias.repository;

import com.gr.geias.dto.NewsCommentDTO;
import com.gr.geias.model.NewsComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsCommentRepository {
    void insertComment(NewsComment comment);

    List<NewsComment> selectCommentsByNewsId(@Param("newsId") Integer newsId);

    List<NewsCommentDTO> selectCommentsWithUserName(
            @Param("newsId") Integer newsId,
            @Param("offset") int offset,
            @Param("size") int size
    );

    int logicDeleteComment(@Param("commentId") Integer commentId);

    List<NewsCommentDTO> selectAllComments(@Param("offset") int offset,
                                           @Param("size") int size,
                                           @Param("includeDeleted") boolean includeDeleted);
}
