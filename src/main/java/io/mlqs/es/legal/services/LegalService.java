package io.mlqs.es.legal.services;

import io.mlqs.es.legal.db.LegalRecordEntity;
import io.mlqs.es.legal.entity.LegalEntity;

import java.util.List;

/**
 * 对所有法律条款进行搜索的接口
 */
public interface LegalService {
    /**
     * 混合检索
     * @param context 检索内容
     * @param vector 检索内容向量化
     * @param threshold 搜索阈值
     * @return 法律条文的列表
     */
    List<LegalRecordEntity> query(String context, List<Float> vector, double threshold);

    /**
     * 获取法律实体类
     * @return 法条实体类
     */
    LegalEntity getLegalEntity();
}
