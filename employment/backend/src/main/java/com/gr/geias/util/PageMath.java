package com.gr.geias.util;

/**
 * 分页计算工具类
 */
public class PageMath {
    /**
     * 将前端的pageNum页码转换为Dao层理解的行索引
     * 
     * @param pageNum 页码
     * @param pageSize 每一页的大小
     * @return 行索引
     */
    public static int pageNumtoRowIndex(int pageNum, int pageSize) {
        return pageNum > 0 ? (pageNum - 1) * pageSize : 0;
    }
} 