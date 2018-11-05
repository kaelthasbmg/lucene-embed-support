package com.github.carsymor.lucene.querySupport.parameter;

import org.apache.lucene.search.Query;

/**
 * Lucene查询参数基类
 *
 * @author Carsymor
 */
public abstract class QueryParameter {

    /**
     * 参数对应索引文件字段的名称
     */
    protected String parameterField;

    public String getParameterField() {
        return parameterField;
    }

    public void setParameterField(String parameterField) {
        this.parameterField = parameterField;
    }

    /**
     * 生成用于Lucene查询的Query对象
     *
     * @return 生成用于Lucene查询的Query对象
     */
    public abstract Query generateQuery();
}
