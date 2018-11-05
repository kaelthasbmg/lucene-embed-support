package com.github.carsymor.lucene.updateSupport;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuceneUpdateTest {

    @Test
    public void updateTest() {
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_update";

        FileUtil.clearFolder(indexFilePath);

        List<Map<String, Object>> testDataList = TestDataUtil.generateMapDate();
        MapIndexSupport mapIndexSupport = new MapIndexSupport();
        mapIndexSupport.batchInsert(testDataList, indexFilePath);

        List<QueryParameter> queryParameters = new ArrayList<>();
        SingleValueParameter queryParameter = new SingleValueParameter("userName", "李连杰");
        queryParameters.add(queryParameter);

        Map<String, String> updateParameters = new HashMap<>();
        updateParameters.put("userName", "李22连杰");
        LuceneUpdate.update(indexFilePath, queryParameters, updateParameters);
        IndexWriterUtil.releaseAllIndexWriter();
        int postCount = LuceneQuery.count(indexFilePath, queryParameters);
        Assert.assertTrue(postCount == 0);
    }
}
