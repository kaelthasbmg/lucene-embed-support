package com.github.carsymor.lucene.querySupport.parameter;

import org.apache.lucene.search.SortField;
import com.github.carsymor.lucene.exceptions.InvalidParameterException;
import com.github.carsymor.lucene.utils.StringUtil;

/**
 * 排序参数
 *
 * @author Carsymor
 */
public class SortParameter {

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 是否按逆序排序
     */
    private boolean desc;

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    public final SortField generateSort() {
        if (StringUtil.isBlank(this.sortField)) {
            throw new InvalidParameterException("无效的排序字段配置，sortField : " + this.sortField);
        }

        return new SortField(this.sortField.toLowerCase(), SortField.Type.STRING, !this.desc);
    }
}
