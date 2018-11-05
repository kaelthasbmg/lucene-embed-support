package com.github.kaelthasbmg.lucene.utils.reflectUtils;

import com.github.kaelthasbmg.lucene.exceptions.InvalidParameterException;
import com.github.kaelthasbmg.lucene.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于解析Bean定义的工具类
 *
 * @author Carsymor
 */
public final class BeanDefineUtil {
    private static Logger logger = LoggerFactory.getLogger(BeanDefineUtil.class);
    private static final Map<String, List<FieldAccessInfo>> fieldDefineMap = new ConcurrentHashMap<>();


    /**
     * 获取类的属性字段定义
     * @param classDefine 类定义
     * @return 属性字段定义
     */
    public static List<FieldAccessInfo> getAttrFields(Class<?> classDefine) {
        String className = classDefine.getName();

        if (fieldDefineMap.get(className) == null) {
            Method[] methods = classDefine.getMethods();
            Map<String, Method> methodMap = new HashMap<>(methods.length);
            if (methods != null) {
                for (Method method : methods) {
                    methodMap.put(method.getName(), method);
                }
            }

            Field[] fields = classDefine.getDeclaredFields();

            List<FieldAccessInfo> analyzableFields = new ArrayList<>();
            Class<?> fieldClass = null;
            FieldAccessInfo fieldAccessInfo = null;
            String fieldName = null;
            for (Field field : fields) {
                fieldClass = field.getType();

                if (isAnalyzable(fieldClass)) {
                    fieldAccessInfo = new FieldAccessInfo();
                    fieldAccessInfo.setField(field);

                    fieldName = field.getName();
                    fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    if (methodMap.get("get" + fieldName) != null) {
                        fieldAccessInfo.setGetterMethod(methodMap.get("get" + fieldName));
                    } else {
                        fieldAccessInfo.setGetterMethod(methodMap.get("is" + fieldName));
                    }

                    fieldAccessInfo.setSetterMethod(methodMap.get("set" + fieldName));

                    analyzableFields.add(fieldAccessInfo);
                }
            }

            fieldDefineMap.put(className, analyzableFields);
        }

        return fieldDefineMap.get(className);
    }

    /**
     * 判断类的值是否可直接解析为String
     * @param classDefine 类定义
     * @return 当类的值是否可直接解析为String，返回true，否则返回false
     */
    private static boolean isAnalyzable(Class<?> classDefine) {
        String className = classDefine.getName();

        if ("int".equalsIgnoreCase(className) || "long".equalsIgnoreCase(className) || "double".equalsIgnoreCase(className)
            || "float".equalsIgnoreCase(className) || "short".equalsIgnoreCase(className) || "boolean".equalsIgnoreCase(className)) {
            return true;
        }

        if (Number.class.isAssignableFrom(classDefine)) {
            return true;
        } else if (Date.class.isAssignableFrom(classDefine)) {
            return true;
        } else if (String.class.isAssignableFrom(classDefine)) {
            return true;
        } else if (Boolean.class.isAssignableFrom(classDefine)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置Bean属性字段的值
     * @param bean 目标bean
     * @param fieldValues 字段值集合
     * @param <T> bean类型
     * @throws ParseException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <T> void setValues(T bean, Map<String, String> fieldValues) throws ParseException, IllegalAccessException, InvocationTargetException {
        String className = bean.getClass().getName();
        List<FieldAccessInfo> fieldAccessInfoList = getAttrFields(bean.getClass());
        
        Map<String, FieldAccessInfo> fieldAccessInfoMap = new HashMap<>(fieldAccessInfoList.size());
        for (FieldAccessInfo fieldAccessInfo : fieldAccessInfoList) {
            fieldAccessInfoMap.put(fieldAccessInfo.getFieldName().toLowerCase(), fieldAccessInfo);
        }

        FieldAccessInfo fieldAccessInfo = null;
        Map.Entry<String, String> entry = null;
        Iterator<Map.Entry<String, String>> iterator = fieldValues.entrySet().iterator();
        String fieldName = null;
        String fieldValue = null;
        while (iterator.hasNext()) {
            entry = iterator.next();

            fieldName = entry.getKey().toLowerCase();
            fieldAccessInfo = fieldAccessInfoMap.get(fieldName);

            if (fieldAccessInfo == null) {
                logger.warn("Bean[" + className + "]不具有属性：" + entry.getKey());
            } else {
                fieldValue = entry.getValue();

                /**
                 * 若文档记录中记录字段数据为null，则对象中字段值使用默认值
                 */
                if (fieldValue != null) {
                    setValue(bean, fieldAccessInfo, fieldValue);
                }
            }
        }
    }

    /**
     * 设置Bean字段值
     * @param bean 目标bean对象
     * @param fieldAccessInfo 对应字段访问信息
     * @param fieldStringValue 字段值(String类型)
     * @param <T> 目标Bean类型
     * @throws ParseException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static <T> void setValue(T bean, FieldAccessInfo fieldAccessInfo, String fieldStringValue) throws ParseException, IllegalAccessException, InvocationTargetException {
        Field field = fieldAccessInfo.getField();
        Object fieldValue = StringUtil.transfer(fieldStringValue, field.getType());

        if (field.isAccessible()) {
            field.set(bean, fieldValue);
        } else if (fieldAccessInfo.hasSetterMethod()) {
            fieldAccessInfo.getSetterMethod().invoke(bean, fieldValue);
        } else {
            throw new InvalidParameterException("无法在类" + bean.getClass().getName() + "中通过反射方式设置属性字段" + field.getName() + "的值，fieldValue：" + fieldStringValue);
        }
    }
}
