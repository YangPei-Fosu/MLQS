package io.mlqs.es;

import io.mlqs.es.entity.DocumentSearchResultEntity;
import io.mlqs.es.legal.services.LegalService;

import java.util.Map;

/**
 * 文档查询接口
 */
public interface DocumentService {
    /**
     * 混合检索检索
     * @param context 检索内容
     * @param legalName 需要使用到的法条名称与使用的关联度阈值
     * @return 检索结果
     */
    DocumentSearchResultEntity hybridSearch(String context, Map<String, Double> legalName);

    /**
     * 获取可以使用的法条名称
     * @return 法条名称Map[枚举名称，中文名称]
     */
    Map<String, String> getUsableLegalName();

    LegalService getLegalService(String name);
}
