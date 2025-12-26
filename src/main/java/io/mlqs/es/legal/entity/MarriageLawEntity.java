package io.mlqs.es.legal.entity;

import io.mlqs.es.legal.db.MarriageLawRecordEntity;

/**
 * 中华人民共和国婚姻法
 */
public class MarriageLawEntity extends LegalEntity<MarriageLawRecordEntity> {
    @Override
    public String getLawName() {
        return "中华人民共和国婚姻法";
    }

    @Override
    public String getInfo() {
        return "1980年9月10日第五届全国人民代表大会第三次会议通过　根据2001年4月28日第九届全国人民代表大会常务委员会第二十一次会议《关于修改﹤中华人民共和国婚姻法﹥的决定》修正，《中华人民共和国婚姻法》在2021年1月1日已经被民法典替代";
    }
}
