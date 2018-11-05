package com.github.kaelthasbmg.lucene.querySupport;

import com.github.kaelthasbmg.lucene.bean.Document;
import com.github.kaelthasbmg.lucene.consts.IndexFileConst;
import com.github.kaelthasbmg.lucene.utils.TestDataUtil;
import com.github.kaelthasbmg.lucene.indexWriter.IndexWriterUtil;
import com.github.kaelthasbmg.lucene.insertSupport.impl.MapIndexSupport;
import com.github.kaelthasbmg.lucene.querySupport.parameter.MultiValuesParameter;
import com.github.kaelthasbmg.lucene.querySupport.parameter.QueryParameter;
import com.github.kaelthasbmg.lucene.querySupport.parameter.RangeValuesParameter;
import com.github.kaelthasbmg.lucene.querySupport.parameter.SingleValueParameter;
import com.github.kaelthasbmg.lucene.utils.FileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LuceneQueryTest {

    @Test
    public void testCount() {
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_query";
        FileUtil.clearFolder(indexFilePath);

        List<Map<String, Object>> testDataList = TestDataUtil.generateMapDate();
        MapIndexSupport mapIndexSupport = new MapIndexSupport();
        mapIndexSupport.batchInsert(testDataList, indexFilePath);
        IndexWriterUtil.releaseAllIndexWriter();

        int count = LuceneQuery.count(indexFilePath, null);
        Assert.assertTrue(count == 10000);
//        System.out.println(LuceneQuery.count(indexFilePath, null));
    }

    @Test
    public void testPageBean() {
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_query";
        FileUtil.clearFolder(indexFilePath);

        List<Map<String, Object>> testDataList = TestDataUtil.generateMapDate();
        MapIndexSupport mapIndexSupport = new MapIndexSupport();
        mapIndexSupport.batchInsert(testDataList, indexFilePath);
        IndexWriterUtil.releaseAllIndexWriter();

        PageData<Document> pageData = LuceneQuery.getDataFrom(indexFilePath, Document.class, null, 1, 50);
        Assert.assertTrue(pageData.getCurrentPageRecordCount() == 50);

//        System.out.println(pageData.getCurrentPageRecordCount());
//
//        Assert.assertTrue(pageData.getCurrentPageRecordCount() == 50);
//        List<Document> dataList = pageData.getData();
//        for (Document document : dataList) {
//            System.out.println(document);
//        }
    }

    @Test
    public void testPageBean2() {
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_query";

        List<QueryParameter> queryParameters = new ArrayList<>();
        SingleValueParameter queryParameter = new SingleValueParameter("userName", "李连杰");
        queryParameters.add(queryParameter);
        MultiValuesParameter multiValuesParameter = new MultiValuesParameter("reportId");
        multiValuesParameter.addParameterValue("5b7fb9a9b90e4976a5f9e0aeded1d181");
        multiValuesParameter.addParameterValue("27cbc5d7f593486585ee2e617f3855ca");
        queryParameters.add(multiValuesParameter);
        RangeValuesParameter rangeValuesParameter = new RangeValuesParameter("checkTime", null, "2018-10-20 07:18:00");
        queryParameters.add(rangeValuesParameter);
        PageData<Document> pageData = LuceneQuery.getDataFrom(indexFilePath, Document.class, queryParameters, 1, 50);

//        System.out.println(pageData.getCurrentPageRecordCount());
//
//        Assert.assertTrue(pageData.getCurrentPageRecordCount() == 50);
//        List<Document> dataList = pageData.getData();
//        for (Document document : dataList) {
//            System.out.println(document);
//        }
    }

    @Test
    public void testGroupCount() {
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_query";
        Map<String, Long> groupCountResults = LuceneQuery.groupCount(indexFilePath, "reportId");
        Iterator<Map.Entry<String, Long>> iterator = groupCountResults.entrySet().iterator();
        Map.Entry<String, Long> groupCountResult = null;

        long totalCount = 0L;
        while (iterator.hasNext()) {
            groupCountResult = iterator.next();
            totalCount += groupCountResult.getValue();
        }

        int totalCount2 = LuceneQuery.count(indexFilePath);
        Assert.assertTrue(totalCount == totalCount2);
    }
}
