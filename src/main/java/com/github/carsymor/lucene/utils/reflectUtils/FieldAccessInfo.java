package com.github.carsymor.lucene.utils.reflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 访问Bean字段所需信息
 *
 * @author Carsymor
 */
public class FieldAccessInfo {
    /**
     * 字段信息
     */
    private Field field;

    /**
     * getter方法
     */
    private Method getterMethod;

    /**
     * setter方法
     */
    private Method setterMethod;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Method getGetterMethod() {
        return getterMethod;
    }

    public void setGetterMethod(Method getterMethod) {
        this.getterMethod = getterMethod;
    }

    public Method getSetterMethod() {
        return setterMethod;
    }

    public void setSetterMethod(Method setterMethod) {
        this.setterMethod = setterMethod;
    }

    /**
     * 是否具备getter方法
     * @return 若field存在对应的getter方法，则返回true，否则返回false
     */
    public boolean hasGetterMethod() {
        return this.getterMethod != null;
    }

    /**
     * 是否具备setter方法
     * @return 若field存在对应的setter方法，则返回true，否则返回false
     */
    public boolean hasSetterMethod() {
        return this.setterMethod != null;
    }

    /**
     * 获取属性字段名
     * @return 属性字段名
     */
    public String getFieldName() {
        if (this.field == null) {
            return null;
        } else {
            return this.field.getName();
        }
    }
}
