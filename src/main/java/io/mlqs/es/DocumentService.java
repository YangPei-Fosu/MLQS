package io.mlqs.es;

import io.mlqs.es.db.DocumentEntity;

import java.util.List;

/**
 * 文档查询接口
 */
public interface DocumentService {
    List<DocumentEntity> knn(String context);
    List<DocumentEntity> hybridSearch(String context);
    void save(DocumentEntity documentEntity);
    void save(List<DocumentEntity> documentEntityList);

}
