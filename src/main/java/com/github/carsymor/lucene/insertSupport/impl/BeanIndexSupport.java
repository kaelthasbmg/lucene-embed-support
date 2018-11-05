package com.github.carsymor.lucene.insertSupport.impl;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.util.BytesRef;
import com.github.carsymor.lucene.consts.FieldConst;
import com.github.carsymor.lucene.exceptions.InvalidParameterException;
import com.github.carsymor.lucene.indexWriter.IndexWriterUtil;
import com.github.carsymor.lucene.insertSupport.ObjectIndexSupport;
import com.github.carsymor.lucene.utils.StringUtil;
import com.github.carsymor.lucene.utils.reflectUtils.BeanDefineUtil;
import com.github.carsymor.lucene.utils.reflectUtils.FieldAccessInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 基于Java Bean的数据索引
 * @param <T> Java Bean的类型
 *
 * @author Carsymor
 */
public class BeanIndexSupport<T> implements ObjectIndexSupport<T> {
    private static Logger log = LoggerFactory.getLogger(BeanIndexSupport.class);

    @Override
    public void insert(T object, String indexFilePath) {
        if (StringUtil.isBlank(indexFilePath)) {
            throw new InvalidParameterException("无效的索引文件存放路径，indexFilePath：" + indexFilePath);
        }

        if (object == null) {
            log.warn("无效的索引目标数据(null)");
        } else {
            List<FieldAccessInfo> attrFields = BeanDefineUtil.getAttrFields(object.getClass());
            if (attrFields.isEmpty()) {
                throw new InvalidParameterException("指定类型无属性定义，或无可直接转换为String值的类型，class：" + object.getClass().getName());
            }

            try {
                Document document = this.createDocument(object, attrFields);
                IndexWriterUtil.insert(indexFilePath, document);
            } catch (Exception e) {
                log.error("索引数据失败", e);
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 根据Bean信息创建索引文件文档数据
     * @param object Bean对象
     * @param attrFields 可直接转换为String值的属性字段
     * @return 索引文件文档数据
     */
    private Document createDocument(T object, List<FieldAccessInfo> attrFields) throws IllegalAccessException, InvocationTargetException {
        Document document = new Document();

        Field field = null;
        Method getterMethod = null;
        String fieldValue = null;
        for (FieldAccessInfo fieldAccessInfo : attrFields) {
            field = fieldAccessInfo.getField();

            if (field.isAccessible()) {
                fieldValue = StringUtil.toString(field.get(object));
            } else if (fieldAccessInfo.hasGetterMethod()) {
                getterMethod = fieldAccessInfo.getGetterMethod();
                fieldValue = StringUtil.toString(getterMethod.invoke(object));
            } else {
                throw new InvalidParameterException("无法从类" + object.getClass().getName() + "中通过反射方式获取属性字段" + field.getName() + "的值");
            }

            if (fieldValue != null) {
                document.add(new SortedDocValuesField(field.getName().toLowerCase(), new BytesRef(fieldValue)));
                document.add(new StringField(field.getName().toLowerCase(), fieldValue, org.apache.lucene.document.Field.Store.YES));
                document.add(new StringField(FieldConst.ORIGINAL_NAME_PREFIX + field.getName().toLowerCase(), field.getName(), org.apache.lucene.document.Field.Store.YES));
            }

        }
        
        return document;
    }

    @Override
    public void batchInsert(Collection<T> objects, String indexFilePath) {
        if (StringUtil.isBlank(indexFilePath)) {
            throw new InvalidParameterException("无效的索引文件存放路径，indexFilePath：" + indexFilePath);
        }

        if (objects == null || objects.isEmpty()) {
            log.warn("无效的索引目标数据(collection为null，或为空)");
        } else {

            Iterator<T> iterator = objects.iterator();
            T sampleObject = iterator.next();

            List<FieldAccessInfo> attrFields = BeanDefineUtil.getAttrFields(sampleObject.getClass());
            if (attrFields.isEmpty()) {
                throw new InvalidParameterException("指定类型无属性定义，或无可直接转换为String值的类型，class：" + sampleObject.getClass().getName());
            }

            try {
                Document document = null;
                List<Document> documents = new ArrayList<>(objects.size());
                for (T object : objects) {
                    document = this.createDocument(object, attrFields);
                    documents.add(document);
                }

                IndexWriterUtil.insert(indexFilePath, documents);

            } catch (Exception e) {
                log.error("索引数据失败", e);
                throw new RuntimeException(e);
            }
        }

    }
}
