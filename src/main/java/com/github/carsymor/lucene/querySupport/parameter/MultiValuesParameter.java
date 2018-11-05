package com.github.carsymor.lucene.querySupport.parameter;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import com.github.carsymor.lucene.exceptions.InvalidParameterException;
import com.github.carsymor.lucene.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询时对于一个字段有多个判断条件
 *
 * @author Carsymor
 */
public class MultiValuesParameter extends QueryParameter {

    /**
     * 参数值
     */
    private List<String> parameterValues;

    /**
     * 是否对参数进行模糊查询
     */
    private boolean fuzzyMatch;

    public MultiValuesParameter(String parameterField) {
        this.parameterField = parameterField;
    }

    public MultiValuesParameter(String parameterField, List<String> parameterValues) {
        this.parameterField = parameterField;
        this.parameterValues = parameterValues;
    }

    public MultiValuesParameter(String parameterField, List<String> parameterValues, boolean fuzzyMatch) {
        this.parameterField = parameterField;
        this.parameterValues = parameterValues;
        this.fuzzyMatch = fuzzyMatch;
    }

    @Override
    public Query generateQuery() {
        if (StringUtil.isBlank(this.parameterField)) {
            throw new InvalidParameterException("无效的查询字段配置，parameterField：" + this.parameterField);
        }

        if (this.parameterValues == null || this.parameterValues.isEmpty()) {
            throw new InvalidParameterException("未设置查询参数值，parameterField：" + this.parameterField);
        }

        BooleanQuery.Builder multiValueQueryBuilder = new BooleanQuery.Builder();
        multiValueQueryBuilder.setMinimumNumberShouldMatch(1);

        Query partialQuery = null;
        for (String parameterValue : this.parameterValues) {
            if (this.fuzzyMatch) {
                partialQuery = new WildcardQuery(new Term(this.parameterField.toLowerCase(), "*" + parameterValue + "*"));
            } else {
                partialQuery = new WildcardQuery(new Term(this.parameterField.toLowerCase(), parameterValue));
            }
            multiValueQueryBuilder.add(partialQuery, BooleanClause.Occur.SHOULD);
        }

        return multiValueQueryBuilder.build();
    }

    public List<String> getParameterValues() {
        return parameterValues;
    }

    public void setParameterValues(List<String> parameterValues) {
        this.parameterValues = parameterValues;
    }

    public boolean isFuzzyMatch() {
        return fuzzyMatch;
    }

    public void setFuzzyMatch(boolean fuzzyMatch) {
        this.fuzzyMatch = fuzzyMatch;
    }

    /**
     * 添加查询参数值
     * @param parameterValue 查询参数值
     */
    public void addParameterValue(String parameterValue) {
        if (this.parameterValues == null) {
            this.parameterValues = new ArrayList<>();
        }
        this.parameterValues.add(parameterValue);
    }
}
