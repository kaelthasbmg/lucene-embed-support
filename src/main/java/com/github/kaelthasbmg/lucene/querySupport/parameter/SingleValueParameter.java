package com.github.kaelthasbmg.lucene.querySupport.parameter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import com.github.kaelthasbmg.lucene.exceptions.InvalidParameterException;
import com.github.kaelthasbmg.lucene.utils.StringUtil;

/**
 * 查询时对于一个字段仅有一个判断条件
 *
 * @author Carsymor
 */
public class SingleValueParameter extends QueryParameter {

    /**
     * 参数值
     */
    private String parameterValue;

    /**
     * 是否对参数进行精确匹配
     */
    private boolean exactMatch;

    public SingleValueParameter(String parameterField, String parameterValue) {
        this.parameterField = parameterField;
        this.parameterValue = parameterValue;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(boolean exactMatch) {
        this.exactMatch = exactMatch;
    }

    @Override
    public Query generateQuery() {

        if (StringUtil.isBlank(this.parameterField)) {
            throw new InvalidParameterException("无效的查询字段配置，parameterField：" + this.parameterField);
        }

        if (StringUtil.isBlank(this.parameterValue)) {
            throw new InvalidParameterException("未设置查询参数值，parameterField：" + this.parameterField);
        }

        if (this.exactMatch) {
            return new WildcardQuery(new Term(this.parameterField.toLowerCase(), this.parameterValue));
        } else {
            return new WildcardQuery(new Term(this.parameterField.toLowerCase(), "*" + this.parameterValue + "*"));
        }
    }
}
