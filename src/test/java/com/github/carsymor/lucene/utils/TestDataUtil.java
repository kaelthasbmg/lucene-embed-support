package com.github.carsymor.lucene.utils;

import com.github.carsymor.lucene.testSupport.TestData;
import com.github.carsymor.lucene.bean.Document;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 生成测试数据的工具类
 */
public final class TestDataUtil {

    public static List<Document> generateBeanData() {
        List<Document> dataList = new ArrayList<>(10000);
        Document data = null;

        long curTime = System.currentTimeMillis() / 1000 * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int englishNameArrayLength = TestData.englishNameArray.length;
        int chineseNameArrayLength = TestData.chineseNameArray.length;
        int fileNameArrayLength = TestData.fileNameArray.length;
        int contentArrayLength = TestData.contentArray.length;

        String reportId = null;
        Random random = new Random();
        for (int i = 0, j = 10000; i < j; i++) {
            if (i % 200 == 0) {
                reportId = UUID.randomUUID().toString().replace("-", "");
            }

            data = new Document();
            data.setPk(curTime + i);
            data.setRecordName("recordName" + i);
            data.setUserName(TestData.chineseNameArray[i % chineseNameArrayLength]);
            data.setAccount(TestData.englishNameArray[i % englishNameArrayLength]);
            data.setFileName(TestData.fileNameArray[i % fileNameArrayLength]);
            data.setFileContent(TestData.contentArray[i % contentArrayLength]);
            data.setCheckTime(new Date(curTime + i / 500 * 3600 * 1000));
            data.setUploaded(i % 2 == 1);
            data.setReportId(reportId);
            data.setMatchTime(random.nextInt());

            dataList.add(data);
        }

        return dataList;
    }

    public static List<Map<String, Object>> generateMapDate() {
        List<Map<String, Object>> dataList = new ArrayList<>(10000);
        Map<String, Object> data = null;

        long curTime = System.currentTimeMillis() / 1000 * 1000;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int englishNameArrayLength = TestData.englishNameArray.length;
        int chineseNameArrayLength = TestData.chineseNameArray.length;
        int fileNameArrayLength = TestData.fileNameArray.length;
        int contentArrayLength = TestData.contentArray.length;

        String reportId = null;
        Random random = new Random();
        for (int i = 0, j = 10000; i < j; i++) {
            if (i % 200 == 0) {
                reportId = UUID.randomUUID().toString().replace("-", "");
            }

            data = new HashMap<>();
            data.put("pk", curTime + i);
            data.put("recordName", "recordName" + i);

            data.put("userName", TestData.chineseNameArray[i % chineseNameArrayLength]);
            data.put("account", TestData.englishNameArray[i % englishNameArrayLength]);
            data.put("fileName", TestData.fileNameArray[i % fileNameArrayLength]);
            data.put("fileContent", TestData.contentArray[i % contentArrayLength]);

            data.put("checkTime", new Date(curTime + i / 500 * 3600 * 1000));
            data.put("uploaded", i % 2 == 1);
            data.put("reportId", reportId);
            data.put("matchTime", random.nextInt());

            dataList.add(data);
        }

        return dataList;
    }
}
