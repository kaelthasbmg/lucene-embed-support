package com.github.carsymor.lucene.utils.reflectUtils;


import com.github.carsymor.lucene.bean.TestBean;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanDefineUtilTest {

    @Test
    public void testGetAttrFields() {
        List<FieldAccessInfo> fieldAccessInfoList = BeanDefineUtil.getAttrFields(TestBean.class);

        Assert.assertTrue(fieldAccessInfoList.size() == 5);
        FieldAccessInfo fieldAccessInfo = fieldAccessInfoList.get(0);
        Assert.assertTrue(fieldAccessInfo.hasGetterMethod());
        Assert.assertTrue(fieldAccessInfo.hasSetterMethod());
    }

    @Test
    public void testSetValues() throws ParseException, IllegalAccessException, InvocationTargetException {
        TestBean testBean = new TestBean();
        Map<String, String> fieldValue = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date();
        Timestamp curTimestamp = new Timestamp(System.currentTimeMillis());

        fieldValue.put("attr1", "attr1Value");
        fieldValue.put("attr2", "11");
        fieldValue.put("attr3", dateFormat.format(curDate));
        fieldValue.put("attr4", "22");
        fieldValue.put("attr5", dateFormat.format(curTimestamp));

        BeanDefineUtil.setValues(testBean, fieldValue);
        Assert.assertTrue("attr1Value".equals(testBean.getAttr1()));
        Assert.assertTrue(testBean.getAttr2() == 11);

        Assert.assertTrue(dateFormat.format(testBean.getAttr3()).equals(dateFormat.format(curDate)));
        Assert.assertTrue(testBean.getAttr4() == 22L);
        Assert.assertTrue(dateFormat.format(testBean.getAttr5()).equals(dateFormat.format(curTimestamp)));
    }
}
