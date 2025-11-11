package io.mlqs.es.entity.legal;

import io.mlqs.es.db.CivilCodeRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 民法典实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CivilCodeEntity extends LegalEntity {
    private final String lawName = "中华人民共和国民法典";

    /**
     * 基本信息
     */
    public final String info = "2020年5月28日第十三届全国人民代表大会第三次会议通过";

    /**
     * 法条列表
     */
    private List<CivilCodeRecordEntity> records;

    @Override
    public String toString() {
        return "CivilCodeEntity{" +
                "lawName='" + lawName + '\'' +
                ", info='" + info + '\'' +
                ", records=" + records +
                '}';
    }
}
