package io.mlqs.es.entity.legal;

import io.mlqs.es.db.MarriageRegistrationRecordEntity;

/**
 * 婚姻登记法实体类
 */

public class MarriageRegistrationEntity extends LegalEntity<MarriageRegistrationRecordEntity>{
    @Override
    public String getLawName() {
        return "婚姻登记条例";
    }

    @Override
    public String getInfo() {
        return "2003年8月8日中华人民共和国国务院令第387号公布　根据2024年12月6日《国务院关于修改和废止部分行政法规的决定》第一次修订　2025年4月6日中华人民共和国国务院令第804号第二次修订";
    }
}
