package com.github.carsymor.lucene.insertSupport;

import com.github.carsymor.lucene.bean.Document;
import com.github.carsymor.lucene.consts.IndexFileConst;
import com.github.carsymor.lucene.utils.TestDataUtil;
import com.github.carsymor.lucene.indexWriter.IndexWriterUtil;
import com.github.carsymor.lucene.insertSupport.impl.BeanIndexSupport;
import com.github.carsymor.lucene.insertSupport.impl.MapIndexSupport;
import com.github.carsymor.lucene.querySupport.LuceneQuery;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LuceneInsertTest {

    @Test
    public void insertMapTest() {
        List<Map<String, Object>> testDataList = TestDataUtil.generateMapDate();
        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_multiInsert";

        int preCount = LuceneQuery.count(indexFilePath, null);

        MapIndexSupport mapIndexSupport = new MapIndexSupport();
        mapIndexSupport.batchInsert(testDataList, indexFilePath);
        IndexWriterUtil.releaseAllIndexWriter();

        int postCount = LuceneQuery.count(indexFilePath, null);

        Assert.assertTrue((postCount - preCount) == 10000);
    }

    @Test
    public void insertBeanTest() {
        List<Document> testDataList = TestDataUtil.generateBeanData();

        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_multiInsert";

        int preCount = LuceneQuery.count(indexFilePath, null);

        BeanIndexSupport<Document> beanIndexSupport = new BeanIndexSupport<>();
        beanIndexSupport.batchInsert(testDataList, indexFilePath);
        IndexWriterUtil.releaseAllIndexWriter();

        int postCount = LuceneQuery.count(indexFilePath, null);

        Assert.assertTrue((postCount - preCount) == 10000);
    }

    @Test
    public void multiInsertTest() throws InterruptedException {
        List<Map<String, Object>> testDataList1 = TestDataUtil.generateMapDate();
        List<Map<String, Object>> testDataList2 = TestDataUtil.generateMapDate();

        String indexFilePath = IndexFileConst.INDEX_FOLDER_PATH + "test_multiInsert";
        int preCount = LuceneQuery.count(indexFilePath, null);

        CountDownLatch countDownLatch = new CountDownLatch(2);

        InsertTask insertTask1 = new InsertTask("task1", indexFilePath, testDataList1, countDownLatch);
        InsertTask insertTask2 = new InsertTask("task2", indexFilePath, testDataList2, countDownLatch);

        ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

        long startTimePoint = System.currentTimeMillis() + 10000;

        service.schedule(insertTask1, startTimePoint - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        service.schedule(insertTask2, startTimePoint - System.currentTimeMillis(), TimeUnit.MILLISECONDS);

        countDownLatch.await();
        IndexWriterUtil.releaseAllIndexWriter();

        int postCount = LuceneQuery.count(indexFilePath, null);

        Assert.assertTrue((postCount - preCount) == 20000);
    }
    private class InsertTask implements Runnable {
        private List<Map<String, Object>> testData;
        private String indexPath;
        private String taskName;
        private CountDownLatch countDownLatch;

        InsertTask(String taskName, String indexPath, List<Map<String, Object>> testData, CountDownLatch countDownLatch) {
            this.taskName = taskName;
            this.indexPath = indexPath;
            this.testData = testData;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                MapIndexSupport mapIndexSupport = new MapIndexSupport();
                mapIndexSupport.batchInsert(this.testData, this.indexPath);
            } catch (Exception e) {
                System.out.println(this.taskName + " | " + e.getMessage());
                throw new RuntimeException(e);
            }
            this.countDownLatch.countDown();
        }
    }


}
