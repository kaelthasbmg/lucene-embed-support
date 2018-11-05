package com.github.carsymor.lucene.utils;

import com.github.carsymor.lucene.utils.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtilTest {

    @Test
    public void testToString() {
        String stringValue = "22123123";
        Assert.assertTrue(stringValue.equals(StringUtil.toString(stringValue)));

        int intValue = 10;
        Assert.assertTrue("10".equals(StringUtil.toString(intValue)));

        long longValue = 20L;
        Assert.assertTrue("20".equals(StringUtil.toString(longValue)));

        Assert.assertTrue("20.01".equals(StringUtil.toString(20.01)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        Assert.assertTrue(dateFormat.format(curDate).equals(StringUtil.toString(curDate)));

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Assert.assertTrue(dateFormat.format(timestamp).equals(StringUtil.toString(timestamp)));

        boolean boolValue = false;
        Assert.assertTrue("0".equals(StringUtil.toString(boolValue)));
    }
}
