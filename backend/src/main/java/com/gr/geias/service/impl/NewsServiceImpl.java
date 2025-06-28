package com.gr.geias.service.impl;

import com.gr.geias.model.News;
import com.gr.geias.repository.NewsRepository;
import com.gr.geias.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Override
    public void publishNews(News news) {
        newsRepository.insertNews(news);
    }

    @Override
    public News getNewsById(Integer newsId) {
        // 先更新阅读量 +1
        newsRepository.incrementReadCount(newsId);
        // 再查询完整新闻内容
        return newsRepository.selectNewsById(newsId);
    }

    @Override
    public int updateNews(News news) {
        return newsRepository.updateNews(news);
    }

    @Override
    public List<News> getNewsList(int offset, int size, String tags) {
        return newsRepository.selectNewsList(offset, size, tags);
    }

    @Override
    public List<News> getHotNews(Integer limit) {
        return newsRepository.selectHotNews(limit);
    }

    @Override
    public List<News> getLatestNews(Integer limit) {
        return newsRepository.selectLatestNews(limit);
    }

    @Override
    public List<News> searchNews(String keyword, int offset, int size) {
        return newsRepository.searchNews(keyword, offset, size);
    }

    @Override
    public int deleteNewsById(Integer newsId) {
        return newsRepository.logicDeleteNews(newsId);
    }
}
