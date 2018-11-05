package com.github.carsymor.lucene.deleteSupport;

import com.github.carsymor.lucene.querySupport.parameter.QueryParameter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Query;
import com.github.carsymor.lucene.exceptions.InvalidParameterException;
import com.github.carsymor.lucene.indexWriter.IndexWriterUtil;
import com.github.carsymor.lucene.utils.FileUtil;
import com.github.carsymor.lucene.utils.QueryUtil;
import com.github.carsymor.lucene.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * 删除操作工具类
 *
 * @author Carsymor
 *
 */
public final class LuceneDelete {
    private static Logger logger = LoggerFactory.getLogger(LuceneDelete.class);

    /**
     * 按照执行查询规则，删除符合条件的索引文件记录
     *
     * @param indexPath 索引文件路径
     * @param queryParameters 查询参数
     */
    public static void delete(String indexPath, List<QueryParameter> queryParameters) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (FileUtil.hasIndexInfo(indexPath)) {
            IndexWriter indexWriter = null;
            try {
                indexWriter = IndexWriterUtil.getIndexWriter(indexPath);
                Query query = QueryUtil.generateQuery(queryParameters);

                indexWriter.deleteDocuments(query);
                indexWriter.commit();

            } catch (Exception e) {
                logger.error("删除指定索引记录失败", e);
                try {
                    indexWriter.rollback();
                } catch (IOException e1) {
                    logger.error("索引文件回滚失败", e);
                }
            } finally {
                indexWriter = null;
            }
        }
    }
}
