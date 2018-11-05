package com.github.kaelthasbmg.lucene.querySupport.parameter;


import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.BytesRef;
import com.github.kaelthasbmg.lucene.exceptions.InvalidParameterException;
import com.github.kaelthasbmg.lucene.utils.StringUtil;

/**
 * 查询时对于一个字段应用返回查询参数(如时间段)
 *
 * @author Carsymor
 */
public class RangeValuesParameter extends QueryParameter {

    /**
     * 范围值的起始部分
     */
    private String beginValue;

    /**
     * 范围值的终止部分
     */
    private String endValue;

    /**
     * 查询结果中是否不包含起始部分值
     */
    private boolean excludeBeginValue;

    /**
     * 查询结果中是否不包含终止部分值
     */
    private boolean excludeEndValue;

    public RangeValuesParameter(String parameterField, String beginValue, String endValue) {
        this.parameterField = parameterField;
        this.beginValue = beginValue;
        this.endValue = endValue;
    }

    public String getBeginValue() {
        return beginValue;
    }

    public void setBeginValue(String beginValue) {
        this.beginValue = beginValue;
    }

    public String getEndValue() {
        return endValue;
    }

    public void setEndValue(String endValue) {
        this.endValue = endValue;
    }

    public boolean isExcludeBeginValue() {
        return excludeBeginValue;
    }

    public void setExcludeBeginValue(boolean excludeBeginValue) {
        this.excludeBeginValue = excludeBeginValue;
    }

    public boolean isExcludeEndValue() {
        return excludeEndValue;
    }

    public void setExcludeEndValue(boolean excludeEndValue) {
        this.excludeEndValue = excludeEndValue;
    }

    @Override
    public Query generateQuery() {
        if (StringUtil.isBlank(this.parameterField)) {
            throw new InvalidParameterException("无效的查询字段配置，parameterField：" + this.parameterField);
        }

        if (StringUtil.isAllBlank(this.beginValue, this.endValue)) {
            throw new InvalidParameterException("无效的范围值配置，parameterField：" + this.parameterField + ", beginValue : "
                + this.beginValue + ", endValue : " + this.endValue);
        }

        BytesRef beginRef = null;
        if (StringUtil.isNotBlank(this.beginValue)) {
            beginRef = new BytesRef(this.beginValue);
        }

        BytesRef endRef = null;
        if (StringUtil.isNotBlank(this.endValue)) {
            endRef = new BytesRef(this.endValue);
        }

        return new TermRangeQuery(this.parameterField.toLowerCase(), beginRef, endRef, !this.excludeBeginValue, !this.excludeEndValue);
    }
}
