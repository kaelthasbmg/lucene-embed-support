package com.github.carsymor.lucene.querySupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询返回结果
 *
 * @param <T> 结果记录类型
 * @author Carsymor
 *
 */
public class PageData<T> {

    /**
     * 本页数据
     */
    private List<T> data;

    /**
     * 总记录数量
     */
    private int total;

    /**
     * 总页码数
     */
    private int totalPageNumber;

    /**
     * 当前页页码
     */
    private int currentPageNumber;

    /**
     * 每页记录数量
     */
    private int pageSize;

    /**
     * 当前页记录数量
     */
    private int currentPageRecordCount;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPageRecordCount() {
        return currentPageRecordCount;
    }

    public void setCurrentPageRecordCount(int currentPageRecordCount) {
        this.currentPageRecordCount = currentPageRecordCount;
    }

    public int getTotalPageNumber() {
        return totalPageNumber;
    }

    public void setTotalPageNumber(int totalPageNumber) {
        this.totalPageNumber = totalPageNumber;
    }

    /**
     * 添加页记录
     * @param resultData 单条记录
     */
    public void addData(T resultData) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }

        this.data.add(resultData);
    }

    /**
     * 计算当前页记录数量
     */
    public void calCurrentPageRecordCount() {
        if (this.data == null) {
            this.currentPageRecordCount = 0;
        } else {
            this.currentPageRecordCount = this.data.size();
        }
    }

    /**
     * 计算总页数
     */
    public void calTotalPageNumber() {
        if (this.total % this.pageSize == 0) {
            this.totalPageNumber = this.total / this.pageSize;
        } else {
            this.totalPageNumber = this.total / this.pageSize + 1;
        }
    }
}
