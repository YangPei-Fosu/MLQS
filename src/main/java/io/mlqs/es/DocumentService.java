package io.mlqs.es;

import io.mlqs.es.entity.DocumentSearchResultEntity;

import java.util.List;

/**
 * 文档查询接口
 */
public interface DocumentService {
    List<DocumentSearchResultEntity> knn(String context);
    List<DocumentSearchResultEntity> hybridSearch(String context);
}
