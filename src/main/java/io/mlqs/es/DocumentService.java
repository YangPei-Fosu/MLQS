package io.mlqs.es;

import io.mlqs.es.entity.DocumentSearchResultEntity;

import java.util.List;
import java.util.Map;

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

    /**
     * 获取可以使用的法条名称
     * @return 法条名称Map[枚举名称，中文名称]
     */
    Map<String, String> getUsableLegalName();
}
