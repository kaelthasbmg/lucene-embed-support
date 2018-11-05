package com.github.kaelthasbmg.lucene.utils;

import com.github.kaelthasbmg.lucene.querySupport.parameter.QueryParameter;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

import java.util.List;

/**
 * 查询条件组装工具类
 *
 * @author Carsymor
 */
public class QueryUtil {

    /**
     * 组合查询条件
     * @param parameters 查询条件清单
     * @return lucene查询对象
     */
    public static Query generateQuery(List<QueryParameter> parameters) {
        Query query = null;
        if (parameters == null || parameters.isEmpty()) {
            query = new MatchAllDocsQuery();
        } else {
            BooleanQuery.Builder builder = new BooleanQuery.Builder();

            for (QueryParameter queryParameter : parameters) {
                builder.add(queryParameter.generateQuery(), BooleanClause.Occur.MUST);
            }
            query = builder.build();
        }

        return query;
    }
}
