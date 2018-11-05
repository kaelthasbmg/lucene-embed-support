package com.github.carsymor.lucene.updateSupport;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.BytesRef;
import com.github.carsymor.lucene.consts.FieldConst;
import com.github.carsymor.lucene.exceptions.InvalidParameterException;
import com.github.carsymor.lucene.indexWriter.IndexWriterUtil;
import com.github.carsymor.lucene.querySupport.LuceneQuery;
import com.github.carsymor.lucene.querySupport.parameter.QueryParameter;
import com.github.carsymor.lucene.utils.QueryUtil;
import com.github.carsymor.lucene.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * 索引文件字段更新工具类
 *
 * @author Carsymor
 */
public final class LuceneUpdate {
    private static Logger logger = LoggerFactory.getLogger(LuceneUpdate.class);


    /**
     * 更新索引文件字段信息
     *
     * @param indexPath 索引文件目录
     * @param queryParameters 查询条件，用于筛选需要更新的索引文档记录
     * @param updateParameters 更新参数[key：更新的字段名，value：更新的字段值]
     */
    public static void update(String indexPath, List<QueryParameter> queryParameters, Map<String, String> updateParameters) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (updateParameters == null || updateParameters.isEmpty()) {
            throw new InvalidParameterException("无效的更新字段配置，updateParameters为null或为空");
        }

        IndexWriter indexWriter = null;
        try {
            List<Map<String, String>> originalRecords = LuceneQuery.getDataFrom(indexPath, queryParameters);
            updateFieldValue(originalRecords, updateParameters);

            indexWriter = IndexWriterUtil.getIndexWriter(indexPath);
            Query query = QueryUtil.generateQuery(queryParameters);

            indexWriter.deleteDocuments(query);

            Document document = null;
            List<Document> documents = new ArrayList<>(originalRecords.size());
            for (Map<String, String> object : originalRecords) {

                if (!object.isEmpty()) {
                    document = createDocument(object);
                    documents.add(document);
                    indexWriter.addDocument(document);
                }
            }

            indexWriter.commit();

        } catch (Exception e) {
            logger.error("更新指定索引记录失败", e);
            try {
                if (indexWriter != null) {
                    indexWriter.rollback();
                }
            } catch (IOException e1) {
                logger.error("索引文件回滚失败", e);
            }
        } finally {
            indexWriter = null;
        }
    }

    /**
     * 根据Map创建索引文件文档记录
     * @param object Map对象
     * @return 索引文件文档记录
     */
    private static Document createDocument(Map<String, String> object) {
        Set<Map.Entry<String, String>> entries = object.entrySet();

        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        Map.Entry<String, String> attrEntry = null;
        Document document = new Document();

        while(iterator.hasNext()) {
            attrEntry = iterator.next();

            if (attrEntry.getValue() != null) {
                document.add(new SortedDocValuesField(attrEntry.getKey().toLowerCase(), new BytesRef(StringUtil.toString(attrEntry.getValue()))));
                document.add(new StringField(attrEntry.getKey().toLowerCase(), attrEntry.getValue(), Field.Store.YES));
                document.add(new StringField(FieldConst.ORIGINAL_NAME_PREFIX + attrEntry.getKey().toLowerCase(), attrEntry.getKey(), Field.Store.YES));
            }
        }

        return document;
    }

    /**
     * 更新字段信息
     *
     * @param originalRecords 原始索引文件记录
     * @param updateParameters 需更新的字段信息
     */
    private static void updateFieldValue(List<Map<String, String>> originalRecords, Map<String, String> updateParameters) {
        for (Map<String, String> originalRecord : originalRecords) {
            originalRecord.putAll(updateParameters);
            removeNullValueEntry(originalRecord);
        }
    }

    /**
     * 清除值为null的字段
     * @param originalRecord
     */
    private static void removeNullValueEntry(Map<String, String> originalRecord) {
        Iterator<Map.Entry<String, String>> iterator = originalRecord.entrySet().iterator();

        Map.Entry<String, String> entry = null;
        while (iterator.hasNext()) {
            entry = iterator.next();

            if (entry.getValue() == null) {
                iterator.remove();
            }
        }
    }
}
