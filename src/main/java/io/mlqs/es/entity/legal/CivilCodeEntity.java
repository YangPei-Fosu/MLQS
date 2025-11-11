package io.mlqs.es.entity.legal;

import io.mlqs.es.db.CivilCodeRecordEntity;

/**
 * 民法典实体类
 */

public class CivilCodeEntity extends LegalEntity<CivilCodeRecordEntity> {
    @Override
    public String getLawName() {
        return "中华人民共和国民法典";
    }

    @Override
    public String getInfo() {
        return "2020年5月28日第十三届全国人民代表大会第三次会议通过";
    }
}
