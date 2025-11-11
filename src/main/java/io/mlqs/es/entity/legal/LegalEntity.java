package io.mlqs.es.entity.legal;

import io.mlqs.es.db.LegalRegulationsEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 法条实体类的基类
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LegalEntity<T extends LegalRegulationsEntity> {
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
}
