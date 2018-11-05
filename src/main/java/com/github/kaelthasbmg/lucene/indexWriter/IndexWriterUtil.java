package com.github.kaelthasbmg.lucene.indexWriter;

import com.github.kaelthasbmg.lucene.exceptions.IndexWriteException;
import com.github.kaelthasbmg.lucene.utils.FileUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * indexWriter工具类
 *
 * @author Carsymor
 */
public final class IndexWriterUtil {

    private static Map<String, IndexWriter> indexWriterMap = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(IndexWriterUtil.class);

    /**
     * 写入单条记录
     * @param indexPath 索引路径
     * @param document 文档记录
     * @throws IOException
     */
    public static void insert(String indexPath, Document document) throws IOException {
        IndexWriter indexWriter = null;
        try {
            indexWriter = getIndexWriter(indexPath);
            indexWriter.addDocument(document);

            indexWriter.commit();
        } catch (Exception e) {
            if (indexWriter != null) {
                indexWriter.rollback();
            }
            throw new IndexWriteException("索引写入失败，indexPath : " + indexPath, e);
        }
    }

    /**
     * 写入多条记录
     * @param indexPath 索引路径
     * @param documents 文档记录
     * @throws IOException
     */
    public static void insert(String indexPath, List<Document> documents) throws IOException {
        IndexWriter indexWriter = null;
        try {
            indexWriter = getIndexWriter(indexPath);
            indexWriter.addDocuments(documents);

            indexWriter.commit();
        } catch (Exception e) {
            if (indexWriter != null) {
                indexWriter.rollback();
            }
            throw new IndexWriteException("索引写入失败，indexPath : " + indexPath, e);
        }
    }

    /**
     * 生成indexWriter
     * @param indexPath 索引文件存放目录
     * @return indexWriter
     */
    public static IndexWriter getIndexWriter(String indexPath) throws IOException {
        FileUtil.createFolder(indexPath);

        IndexWriter indexWriter = null;
        synchronized (indexWriterMap) {
            indexWriter = indexWriterMap.get(indexPath);
            if (indexWriter == null || !indexWriter.isOpen()) {
                indexWriter = createIndexWriter(indexPath);
                indexWriterMap.put(indexPath, indexWriter);
            }

            indexWriter = indexWriterMap.get(indexPath);
        }

        return indexWriter;
    }

    /**
     * 创建IndexWriter
     * @param indexPath 索引目录
     * @return IndexWriter
     */
    private static IndexWriter createIndexWriter(String indexPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexPath));

        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        return new IndexWriter(indexDirectory, indexWriterConfig);
    }

    /**
     * 释放indexWriter
     */
    public static void releaseAllIndexWriter() {
        synchronized (indexWriterMap) {
            Iterator<Map.Entry<String, IndexWriter>> iterator = indexWriterMap.entrySet().iterator();
            Map.Entry<String, IndexWriter> entry = null;
            String key = null;
            IndexWriter indexWriter = null;

            while (iterator.hasNext()) {
                entry = iterator.next();
                key = entry.getKey();
                indexWriter = entry.getValue();

                if (indexWriter != null) {
                    try {
                        indexWriter.close();
                    } catch (Exception e) {
                        logger.warn("indexWriter关闭失败，indexPath : " + key);
                    }
                }
                iterator.remove();
            }
        }
    }
}
