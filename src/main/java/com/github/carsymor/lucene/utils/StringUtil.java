package com.github.carsymor.lucene.utils;

import com.github.carsymor.lucene.exceptions.UnSupportValueTypeException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 字符串工具类
 *
 * @author Carsymor
 */
public final class StringUtil {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 是否为空字符串(连续空白符，或null)
     * @param string 字符串值
     * @return 若为连续空白符，或null，则返回true，否则返回false
     */
    public static boolean isBlank(String string) {
        return string == null || string.trim().equalsIgnoreCase("");
    }

    /**
     * 转换对象至String格式
     * @param object 未知类型对象
     * @return object对应的String值
     */
    public static String toString(Object object) {
        if (object == null) {
            return null;
        } else if (object instanceof String) {
            return (String) object;
        } else if (object instanceof Number) {
            return object.toString();
        } else if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? "1" : "0";
        } else if (object instanceof Date) {
            return dateFormat.format(object);
        } else {
            throw new UnSupportValueTypeException("无法转换为String值的对象类型，object class：" + object.getClass().getName());
        }
    }

    /**
     * 判断给定字符串值是否全部为空
     * @param strings 字符串值(0-n个)
     * @return 若全部为空时，返回true，否则返回false
     */
    public static boolean isAllBlank(String... strings) {
        if (strings == null) {
            return true;
        } else {
            boolean isAllBlank = true;

            for (String string : strings) {
                isAllBlank = isAllBlank && isBlank(string);
            }

            return isAllBlank;
        }
    }

    /**
     * 是否为非空字符串(即不为连续空白符，也不为null)
     * @param string 字符串值
     * @return 若为连续空白符，或null，则返回false，否则返回true
     */
    public static boolean isNotBlank(String string) {
        return string != null && !string.trim().equals("");
    }

    /**
     * 转换字符串值至指定类型
     * @param stringValue 字符串值
     * @param type 目标类型
     * @return 指定类型的值
     */
    public static Object transfer(String stringValue, Class<?> type) throws ParseException {
        if (String.class.isAssignableFrom(type)) {
            return stringValue;
        } else if (Timestamp.class.isAssignableFrom(type)) {
            return new Timestamp(dateFormat.parse(stringValue).getTime());
        } else if (java.sql.Date.class.isAssignableFrom(type)) {
            return new java.sql.Date(dateFormat.parse(stringValue).getTime());
        } else if (Date.class.isAssignableFrom(type)) {
            return dateFormat.parse(stringValue);
        } else if (Boolean.class.isAssignableFrom(type) || "boolean".equals(type.getName())) {
            return "1".equals(stringValue);
        } else if (Integer.class.isAssignableFrom(type) || "int".equals(type.getName())) {
            return Integer.parseInt(stringValue);
        } else if (Long.class.isAssignableFrom(type) || "long".equals(type.getName())) {
            return Long.parseLong(stringValue);
        } else if (Double.class.isAssignableFrom(type) || "double".equals(type.getName())) {
            return Double.parseDouble(stringValue);
        } else if (Float.class.isAssignableFrom(type) || "float".equals(type.getName())) {
            return Float.parseFloat(stringValue);
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return new BigDecimal(stringValue);
        } else if (Short.class.isAssignableFrom(type) || "short".equals(type.getName())) {
            return Short.parseShort(stringValue);
        } else {
            throw new UnSupportValueTypeException("无法转换的对象类型，object class：" + type.getName());
        }
    }
}
