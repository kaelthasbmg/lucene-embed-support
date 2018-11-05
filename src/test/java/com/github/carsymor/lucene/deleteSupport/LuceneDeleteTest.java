package com.github.carsymor.lucene.deleteSupport;

import com.github.carsymor.lucene.consts.IndexFileConst;
import com.github.carsymor.lucene.utils.TestDataUtil;
import com.github.carsymor.lucene.indexWriter.IndexWriterUtil;
import com.github.carsymor.lucene.insertSupport.impl.MapIndexSupport;
import com.github.carsymor.lucene.querySupport.LuceneQuery;
import com.github.carsymor.lucene.querySupport.parameter.QueryParameter;
import com.github.carsymor.lucene.querySupport.parameter.SingleValueParameter;
import com.github.carsymor.lucene.utils.FileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuceneDeleteTest {

    @Test
    public void deleteTest() {
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_delete";
        FileUtil.clearFolder(indexFilePath);

        List<Map<String, Object>> testDataList = TestDataUtil.generateMapDate();
        MapIndexSupport mapIndexSupport = new MapIndexSupport();
        mapIndexSupport.batchInsert(testDataList, indexFilePath);

        int preCount = LuceneQuery.count(indexFilePath, null);

        List<QueryParameter> queryParameters = new ArrayList<>();
        SingleValueParameter queryParameter = new SingleValueParameter("userName", "李连杰");
        queryParameters.add(queryParameter);

        int targetCount = LuceneQuery.count(indexFilePath, queryParameters);

        LuceneDelete.delete(indexFilePath, queryParameters);

        int postCount = LuceneQuery.count(indexFilePath, null);
        Assert.assertTrue((preCount - targetCount) == postCount);

        IndexWriterUtil.releaseAllIndexWriter();
    }
}
