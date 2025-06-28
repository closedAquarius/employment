package com.gr.geias.service;

import com.gr.geias.model.News;

import java.util.List;

public interface NewsService {
    void publishNews(News news);

    News getNewsById(Integer newsId);

    int updateNews(News news);

    List<News> getNewsList(int offset, int size, String tags);

    List<News> getHotNews(Integer limit);

    List<News> getLatestNews(Integer limit);

    List<News> searchNews(String keyword, int offset, int size);

    int deleteNewsById(Integer newsId);
}
