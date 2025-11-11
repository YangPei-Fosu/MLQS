package io.mlqs.es.entity.legal;

import io.mlqs.es.db.LegalRegulationsEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 法条实体类的基类
 */
@NoArgsConstructor
@AllArgsConstructor
public class LegalEntity {
    private String lawName;
    private List<LegalRegulationsEntity> records;
    private String info;

    @Override
    public String toString() {
        return "LegalEntity{" +
                "lawName='" + lawName + '\'' +
                ", info='" + info + '\'' +
                ", records=" + records +
                '}';
    }
}
