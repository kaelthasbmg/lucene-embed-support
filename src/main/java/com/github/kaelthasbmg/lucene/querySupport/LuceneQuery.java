package com.github.kaelthasbmg.lucene.querySupport;

import com.github.kaelthasbmg.lucene.querySupport.parameter.QueryParameter;
import com.github.kaelthasbmg.lucene.querySupport.parameter.SortParameter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.*;
import org.apache.lucene.search.grouping.GroupDocs;
import org.apache.lucene.search.grouping.GroupingSearch;
import org.apache.lucene.search.grouping.TopGroups;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import com.github.kaelthasbmg.lucene.consts.FieldConst;
import com.github.kaelthasbmg.lucene.exceptions.InvalidParameterException;
import com.github.kaelthasbmg.lucene.utils.FileUtil;
import com.github.kaelthasbmg.lucene.utils.QueryUtil;
import com.github.kaelthasbmg.lucene.utils.StringUtil;
import com.github.kaelthasbmg.lucene.utils.reflectUtils.BeanDefineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于索引文件的查询
 *
 * @author Carsymor
 */
public final class LuceneQuery {
    private static Logger logger = LoggerFactory.getLogger(LuceneQuery.class);

    /**
     * 查询记录分组和
     *
     * @param indexPath lucene索引文件存储位置
     * @param groupField 分组字段
     * @param queryParameterList 查询参数
     * @return 记录分组和
     */
    public static Map<String, Long> groupCount(String indexPath, String groupField, List<QueryParameter> queryParameterList) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        Map<String, Long> groupCount = new HashMap<>();
        if (FileUtil.hasIndexInfo(indexPath)) {
            IndexReader indexReader = null;
            Directory directory = null;

            try {
                directory = FSDirectory.open(Paths.get(indexPath));
                indexReader = DirectoryReader.open(directory);

                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                GroupingSearch groupingSearch = new GroupingSearch(groupField.toLowerCase());
                groupingSearch.setAllGroups(true);
                Query query = QueryUtil.generateQuery(queryParameterList);

                TopGroups<BytesRef> result = groupingSearch.search(indexSearcher, query, 0, 100000);
                for (GroupDocs<BytesRef> groupDocs : result.groups){
                    if (groupDocs != null) {
                        groupCount.put(groupDocs.groupValue.utf8ToString(), groupDocs.totalHits);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (indexReader != null) {
                    try {
                        indexReader.close();
                    } catch (IOException e) {
                        logger.error("lucene index reader关闭失败", e);
                    }
                }

                if (directory != null) {
                    try {
                        directory.close();
                    } catch (IOException e) {
                        logger.error("directory关闭失败", e);
                    }
                }
            }
        }

        return groupCount;
    }
    /**
     * 查询记录分组和
     *
     * @param indexPath lucene索引文件存储位置
     * @param groupField 分组字段
     * @return 记录分组和
     */
    public static Map<String, Long> groupCount(String indexPath, String groupField) {
        return groupCount(indexPath, groupField, null);
    }


    /**
     * 查询索引记录数量
     * @param indexPath lucene索引文件存储位置
     * @return 索引记录数量
     */
    public static int count(String indexPath) {
        return count(indexPath, null);
    }

    /**
     * 查询记录数量
     *
     * @param indexPath lucene索引文件存储位置
     * @param queryParameters 查询参数
     * @return 记录数量
     */
    public static int count(String indexPath, List<QueryParameter> queryParameters) {
        int count = 0;

        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (FileUtil.hasIndexInfo(indexPath)) {
            IndexReader indexReader = null;
            Directory directory = null;

            try {
                directory = FSDirectory.open(Paths.get(indexPath));
                indexReader = DirectoryReader.open(directory);

                Query query = QueryUtil.generateQuery(queryParameters);
                IndexSearcher indexSearcher = new IndexSearcher(indexReader);
                count = indexSearcher.count(query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (indexReader != null) {
                    try {
                        indexReader.close();
                    } catch (IOException e) {
                        logger.error("lucene index reader关闭失败", e);
                    }
                }

                if (directory != null) {
                    try {
                        directory.close();
                    } catch (IOException e) {
                        logger.error("directory关闭失败", e);
                    }
                }
            }
        }

        return count;
    }

    /**
     * 根据lucene进行查询
     *
     * @param indexPath lucene索引文件存储位置
     * @param queryParameters 查询参数
     * @return 符合条件的所有记录
     */
    public static List<Map<String, String>> getDataFrom(String indexPath, List<QueryParameter> queryParameters) {
        return getDataFrom(indexPath, queryParameters, null);
    }

    /**
     * 根据lucene进行查询
     *
     * @param indexPath lucene索引文件存储位置
     * @param beanClass 结果Bean的类型定义
     * @param queryParameters 查询参数
     * @param <T> 结果Bean的类型定义
     * @return 符合条件的所有记录
     */
    public static <T> List<T> getDataFrom(String indexPath, Class<T> beanClass, List<QueryParameter> queryParameters) {
        return getDataFrom(indexPath, beanClass, queryParameters, null);
    }

    /**
     * 根据lucene进行查询，返回指定类型的Bean集合
     *
     * @param indexPath lucene索引文件存储位置
     * @param beanClass 结果Bean的类型定义
     * @param queryParameters 查询参数
     * @param sortParameters 排序参数
     * @param <T> 结果Bean的类型定义
     * @return 指定类型的Bean集合
     */
    public static <T> List<T> getDataFrom(String indexPath, Class<T> beanClass, List<QueryParameter> queryParameters, List<SortParameter> sortParameters) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (beanClass == null) {
            throw new InvalidParameterException("未指定返回结果Bean类型");
        }

        if (!FileUtil.hasIndexInfo(indexPath)) {
            return new ArrayList<T>();
        }

        IndexReader indexReader = null;
        Directory directory = null;
        List<T> results = null;

        try {
            directory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(directory);

            Query query = QueryUtil.generateQuery(queryParameters);
            Sort sort = generateSort(sortParameters);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            int resultCount = indexSearcher.count(query);

            TopDocs hits = null;
            if (sort == null) {
                hits = indexSearcher.search(query, resultCount);
            } else {
                hits = indexSearcher.search(query, resultCount, sort);
            }
            ScoreDoc[] resultDocs = hits.scoreDocs;

            T resultData = null;
            Document document = null;
            results = new ArrayList<>(resultCount);
            for (ScoreDoc scoreDoc : resultDocs) {
                document = indexSearcher.doc(scoreDoc.doc);
                resultData = transformDocumentToObject(document, beanClass);

                results.add(resultData);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    logger.error("lucene index reader关闭失败", e);
                }
            }

            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    logger.error("directory关闭失败", e);
                }
            }
        }

        return results;
    }

    /**
     * 转换文档至指定类型的Bean
     * @param document 索引文件文档记录
     * @param beanClass Bean类型
     * @param <T> Bean类型
     * @return 指定类型的Bean
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    private static <T> T transformDocumentToObject(Document document, Class<T> beanClass)
            throws IllegalAccessException, InstantiationException, InvocationTargetException, ParseException {
        T bean = beanClass.newInstance();
        Map<String, String> documentInfo = transformDocumentToMap(document);
        BeanDefineUtil.setValues(bean, documentInfo);

        return bean;
    }


    /**
     * 根据lucene进行查询
     *
     * @param indexPath lucene索引文件存储位置
     * @param queryParameters 查询参数
     * @param sortParameters 排序参数
     * @return 符合条件的所有记录
     */
    public static List<Map<String, String>> getDataFrom(String indexPath, List<QueryParameter> queryParameters, List<SortParameter> sortParameters) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (!FileUtil.hasIndexInfo(indexPath)) {
            return new ArrayList<>();
        }

        IndexReader indexReader = null;
        Directory directory = null;
        List<Map<String, String>> results = null;

        try {
            directory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(directory);

            Query query = QueryUtil.generateQuery(queryParameters);
            Sort sort = generateSort(sortParameters);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            int resultCount = indexSearcher.count(query);

            if (resultCount > 0) {
                TopDocs hits = null;
                if (sort == null) {
                    hits = indexSearcher.search(query, resultCount);
                } else {
                    hits = indexSearcher.search(query, resultCount, sort);
                }
                ScoreDoc[] resultDocs = hits.scoreDocs;

                Map<String, String> resultData = null;
                Document document = null;
                results = new ArrayList<>(resultCount);
                for (ScoreDoc scoreDoc : resultDocs) {
                    document = indexSearcher.doc(scoreDoc.doc);
                    resultData = transformDocumentToMap(document);

                    results.add(resultData);
                }
            } else {
                results = new ArrayList<>();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    logger.error("lucene index reader关闭失败", e);
                }
            }

            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    logger.error("directory关闭失败", e);
                }
            }
        }

        return results;
    }

    /**
     * 根据lucene进行分页查询
     *
     * @param indexPath 索引文件路径
     * @param beanClass 结果Bean类型
     * @param queryParameters 查询参数
     * @param currentPageNumber 当前显示数据页页码
     * @param pageSize 每页记录数量
     * @param <T> 结果Bean类型
     * @return 分页查询结果
     */
    public static <T> PageData<T> getDataFrom(String indexPath, Class<T> beanClass, List<QueryParameter> queryParameters, int currentPageNumber, int pageSize) {
        return getDataFrom(indexPath, beanClass, queryParameters, null, currentPageNumber, pageSize);
    }


    /**
     * 根据lucene进行分页查询
     *
     * @param indexPath 索引文件路径
     * @param beanClass 结果Bean类型
     * @param queryParameters 查询参数
     * @param sortParameters 排序参数
     * @param currentPageNumber 当前显示数据页页码
     * @param pageSize 每页记录数量
     * @param <T> 结果Bean类型
     * @return 分页查询结果
     */
    public static <T> PageData<T> getDataFrom(String indexPath, Class<T> beanClass,
                                              List<QueryParameter> queryParameters, List<SortParameter> sortParameters,
                                              int currentPageNumber, int pageSize) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (beanClass == null) {
            throw new InvalidParameterException("未指定返回结果Bean类型");
        }

        if (currentPageNumber < 0) {
            throw new InvalidParameterException("无效的分页参数配置，currentPageNumber : " + currentPageNumber);
        }

        if (pageSize <= 0) {
            throw new InvalidParameterException("无效的分页参数配置，pageSize : " + pageSize);
        }

        PageData<T> pageData = new PageData<>();
        pageData.setCurrentPageNumber(currentPageNumber);
        pageData.setPageSize(pageSize);

        if (!FileUtil.hasIndexInfo(indexPath)) {
            pageData.setData(new ArrayList<T>());
            pageData.setTotal(0);
            return pageData;
        }

        IndexReader indexReader = null;
        Directory directory = null;

        try {
            directory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(directory);

            int startPos = (currentPageNumber - 1) * pageSize;
            int endPos = currentPageNumber * pageSize;
            int totalCount = 0;

            Query query = QueryUtil.generateQuery(queryParameters);
            Sort sort = generateSort(sortParameters);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            totalCount = indexSearcher.count(query);

            pageData.setTotal(totalCount);

            if (endPos > totalCount) {
                endPos = totalCount;
            }

            if (endPos > 0) {
                TopDocs hits = null;
                if (sort == null) {
                    hits = indexSearcher.search(query, endPos);
                } else {
                    hits = indexSearcher.search(query, endPos, sort);
                }
                ScoreDoc[] resultDocs = hits.scoreDocs;

                if (resultDocs == null) {
                    pageData.setData(new ArrayList<T>());
                } else {

                    Document document = null;
                    T resultData = null;

                    while (startPos < endPos) {
                        document = indexSearcher.doc(resultDocs[startPos].doc);
                        resultData = transformDocumentToObject(document, beanClass);

                        pageData.addData(resultData);

                        startPos++;
                    }
                }
            }

            pageData.calCurrentPageRecordCount();
            pageData.calTotalPageNumber();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    logger.error("lucene index reader关闭失败", e);
                }
            }

            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    logger.error("directory关闭失败", e);
                }
            }
        }

        return pageData;
    }


    /**
     * 根据lucene进行分页查询
     *
     * @param indexPath lucene索引文件存储位置
     * @param queryParameters 查询参数
     * @param currentPageNumber 当前数据页页码
     * @param pageSize 每页记录数
     * @return 单页数据
     */
    public static PageData<Map<String, String>> getDataFrom(String indexPath, List<QueryParameter> queryParameters, int currentPageNumber, int pageSize) {
        return getDataFrom(indexPath, queryParameters, null, currentPageNumber, pageSize);
    }


    /**
     * 根据lucene进行分页查询
     *
     * @param indexPath lucene索引文件存储位置
     * @param queryParameters 查询参数
     * @param sortParameters 排序参数
     * @param currentPageNumber 当前数据页页码
     * @param pageSize 每页记录数
     * @return 单页数据
     */
    public static PageData<Map<String, String>> getDataFrom(String indexPath, List<QueryParameter> queryParameters,
                                                            List<SortParameter> sortParameters, int currentPageNumber, int pageSize) {
        if (StringUtil.isBlank(indexPath)) {
            throw new InvalidParameterException("无效的索引文件路径配置，indexPath : " + indexPath);
        }

        if (currentPageNumber < 0) {
            throw new InvalidParameterException("无效的分页参数配置，currentPageNumber : " + currentPageNumber);
        }

        if (pageSize <= 0) {
            throw new InvalidParameterException("无效的分页参数配置，pageSize : " + pageSize);
        }

        PageData<Map<String, String>> pageData = new PageData<>();
        pageData.setCurrentPageNumber(currentPageNumber);
        pageData.setPageSize(pageSize);

        if (!FileUtil.hasIndexInfo(indexPath)) {
            pageData.setData(new ArrayList<Map<String, String>>());
            pageData.setTotal(0);
            return pageData;
        }

        IndexReader indexReader = null;
        Directory directory = null;

        try {
            directory = FSDirectory.open(Paths.get(indexPath));
            indexReader = DirectoryReader.open(directory);

            int startPos = (currentPageNumber - 1) * pageSize;
            int endPos = currentPageNumber * pageSize;
            int totalCount = 0;

            pageData.setCurrentPageNumber(currentPageNumber);
            pageData.setPageSize(pageSize);

            Query query = QueryUtil.generateQuery(queryParameters);
            Sort sort = generateSort(sortParameters);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            totalCount = indexSearcher.count(query);
            pageData.setTotal(totalCount);

            if (endPos > totalCount) {
                endPos = totalCount;
            }

            TopDocs hits = null;
            if (sort == null) {
                hits = indexSearcher.search(query, endPos);
            } else {
                hits = indexSearcher.search(query, endPos, sort);
            }
            ScoreDoc[] resultDocs = hits.scoreDocs;

            if (resultDocs == null) {
                pageData.setData(new ArrayList<Map<String, String>>());
            } else {

                Document document = null;
                Map<String, String> resultData = null;

                while (startPos < endPos) {
                    document = indexSearcher.doc(resultDocs[startPos].doc);
                    resultData = transformDocumentToMap(document);

                    pageData.addData(resultData);

                    startPos++;
                }
            }
            pageData.calCurrentPageRecordCount();
            pageData.calTotalPageNumber();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (indexReader != null) {
                try {
                    indexReader.close();
                } catch (IOException e) {
                    logger.error("lucene index reader关闭失败", e);
                }
            }

            if (directory != null) {
                try {
                    directory.close();
                } catch (IOException e) {
                    logger.error("directory关闭失败", e);
                }
            }
        }

        return pageData;
    }

    /**
     * 生成查询排序参数
     * @param sortParameters 排序参数
     * @return 查询排序参数
     */
    private static Sort generateSort(List<SortParameter> sortParameters) {
        if (sortParameters == null || sortParameters.isEmpty()) {
            return null;
        } else {
            Sort sort = new Sort();
            SortField[] sortFields = new SortField[sortParameters.size()];
            for (int i = 0, j = sortParameters.size(); i < j; i++) {
                sortFields[i] = sortParameters.get(i).generateSort();
            }
            sort.setSort(sortFields);

            return sort;
        }
    }

    /**
     * 转换索引文件文档记录至Map
     * @param document 索引文件文档记录
     * @return Map[key : 索引文件field名，value : 索引文件field值]
     */
    private static Map<String, String> transformDocumentToMap(Document document) {
        List<IndexableField> fields = document.getFields();

        Map<String, String> originalFieldNameMap = new HashMap<>(fields.size() / 2);
        for (IndexableField field : fields) {
            if (field.name().startsWith(FieldConst.ORIGINAL_NAME_PREFIX)) {
                originalFieldNameMap.put(field.name().replace(FieldConst.ORIGINAL_NAME_PREFIX, ""), field.stringValue());
            }
        }

        Map<String, String> data = new HashMap<>(fields.size() / 2);

        for (IndexableField field : fields) {
            if (!field.name().startsWith(FieldConst.ORIGINAL_NAME_PREFIX)) {
                data.put(originalFieldNameMap.get(field.name()), field.stringValue());
            }
        }

        return data;
    }
}
