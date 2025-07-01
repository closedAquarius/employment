package com.gr.geias.repository;

import com.gr.geias.model.News;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsRepository {
    void insertNews(News news);

    void incrementReadCount(@Param("newsId") Integer newsId);

    News selectNewsById(@Param("newsId") Integer newsId);

    int updateNews(News news);

    List<News> selectNewsList(@Param("offset") int offset,
                              @Param("size") int size,
                              @Param("tags") String tags);

    List<News> selectHotNews(@Param("limit") Integer limit);

    List<News> selectLatestNews(@Param("limit") Integer limit);

    List<News> searchNews(@Param("keyword") String keyword,
                          @Param("offset") int offset,
                          @Param("size") int size);

    int logicDeleteNews(@Param("newsId") Integer newsId);
}
