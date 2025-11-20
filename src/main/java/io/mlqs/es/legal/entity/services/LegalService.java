package io.mlqs.es.legal.entity.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;

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
    List<LegalRecordEntity> knn(String context);

    /**
     * 获取法律实体类
     * @return 法条实体类
     */
    LegalEntity getLegalEntity();
}
