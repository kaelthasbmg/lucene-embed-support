package com.github.kaelthasbmg.lucene.insertSupport;


import java.util.Collection;

/**
 * 基于对象的搜索索引创建支持
 *
 * @param <T> 对象类型
 * @author Carsymor
 */
public interface ObjectIndexSupport<T> {

    /**
     * 在索引文件中新增记录
     * @param object 记录数据
     * @param indexFilePath 索引文件生成位置
     */
    void insert(T object, String indexFilePath);

    /**
     * 在索引文件中批量新增记录
     * @param objects 记录数据(多条)
     * @param indexFilePath 索引文件生成位置
     */
    void batchInsert(Collection<T> objects, String indexFilePath);
}
