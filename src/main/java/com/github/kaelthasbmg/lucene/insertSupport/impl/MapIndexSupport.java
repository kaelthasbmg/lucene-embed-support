package com.github.kaelthasbmg.lucene.insertSupport.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.util.BytesRef;
import com.github.kaelthasbmg.lucene.consts.FieldConst;
import com.github.kaelthasbmg.lucene.exceptions.InvalidParameterException;
import com.github.kaelthasbmg.lucene.indexWriter.IndexWriterUtil;
import com.github.kaelthasbmg.lucene.insertSupport.ObjectIndexSupport;
import com.github.kaelthasbmg.lucene.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * 基于Map的数据存储格式的数据索引
 *
 * @author Carsymor
 */
public class MapIndexSupport implements ObjectIndexSupport<Map<String, Object>> {
    private static Logger log = LoggerFactory.getLogger(MapIndexSupport.class);

    @Override
    public void insert(Map<String, Object> object, String indexFilePath) {
        if (StringUtil.isBlank(indexFilePath)) {
            throw new InvalidParameterException("无效的索引文件存放路径，indexFilePath：" + indexFilePath);
        }

        if (object == null || object.isEmpty()) {
            log.warn("无效的索引目标数据(object为null，或为空)");
        } else {
            try {
                Document document = this.createDocument(object);
                IndexWriterUtil.insert(indexFilePath, document);
            } catch (Exception e) {
                log.error("索引数据失败", e);
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 根据json对象创建索引文件文档数据
     * @param object map对象
     * @return 索引文件文档数据
     */
    private Document createDocument(Map<String, Object> object) {
        Set<Map.Entry<String, Object>> entries = object.entrySet();

        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        Map.Entry<String, Object> attrEntry = null;
        Document document = new Document();

        while(iterator.hasNext()) {
            attrEntry = iterator.next();

            if (attrEntry.getValue() != null) {
                document.add(new SortedDocValuesField(attrEntry.getKey().toLowerCase(), new BytesRef(StringUtil.toString(attrEntry.getValue()))));
                document.add(new StringField(attrEntry.getKey().toLowerCase(), StringUtil.toString(attrEntry.getValue()), Field.Store.YES));
                document.add(new StringField(FieldConst.ORIGINAL_NAME_PREFIX + attrEntry.getKey().toLowerCase(), attrEntry.getKey(), Field.Store.YES));
            }

        }

        return document;
    }

    @Override
    public void batchInsert(Collection<Map<String, Object>> objects, String indexFilePath) {
        if (StringUtil.isBlank(indexFilePath)) {
            throw new InvalidParameterException("无效的索引文件存放路径，indexFilePath：" + indexFilePath);
        }

        if (objects == null || objects.isEmpty()) {
            log.warn("无效的索引目标数据(collection为null，或为空)");
        } else {
            try {
                Document document = null;
                List<Document> documents = new ArrayList<>(objects.size());
                for (Map<String, Object> object : objects) {

                    if (!object.isEmpty()) {
                        document = this.createDocument(object);
                        documents.add(document);
                    }
                }

                IndexWriterUtil.insert(indexFilePath, documents);
            } catch (Exception e) {
                log.error("索引数据失败", e);
                throw new RuntimeException(e);
            }
        }
    }
}
