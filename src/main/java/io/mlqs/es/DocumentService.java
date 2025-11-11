package io.mlqs.es;

import io.mlqs.es.entity.DocumentSearchResultEntity;

import java.util.List;

/**
 * 文档查询接口
 */
public interface DocumentService {
    /**
     * 基于Knn向量化检索
     * @param context 检索内容
     * @param legalName 需要使用到的法条名称
     * @return 检索结果
     */
    DocumentSearchResultEntity knn(String context, List<String> legalName);
    DocumentSearchResultEntity hybridSearch(String context, List<String> legalName);
}
