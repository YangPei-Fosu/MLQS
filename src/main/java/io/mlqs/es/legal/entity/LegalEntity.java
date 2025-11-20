package io.mlqs.es.legal.entity;

import io.mlqs.es.legal.db.LegalRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 法条实体类的基类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LegalEntity<T extends LegalRecordEntity> {
    /**
     * 文献名
     */
    private String lawName;
    /**
     * 法条列表
     */
    private List<T> records;
    /**
     * 基本信息
     */
    private String info;
    /**
     * 拓展字段
     */
    private Map<String, Object> extend = new HashMap<>();
}
