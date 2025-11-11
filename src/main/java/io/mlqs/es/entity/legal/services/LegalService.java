package io.mlqs.es.entity.legal.services;

import io.mlqs.es.db.LegalRegulationsEntity;
import io.mlqs.es.entity.legal.LegalEntity;

import java.util.List;

/**
 * 对所有法律条款进行搜索的接口
 */
public interface LegalService {
    /**
     * 基于Knn向量化检索
     * @param context
     * @return 法律条文的列表
     */
    List<LegalRegulationsEntity> knn(String context);

    /**
     * 获取法律实体类
     * @return 法条实体类
     */
    LegalEntity getLegalEntity();
}
