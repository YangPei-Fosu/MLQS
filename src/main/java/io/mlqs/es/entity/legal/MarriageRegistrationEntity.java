package io.mlqs.es.entity.legal;

import io.mlqs.es.db.MarriageRegistrationRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 婚姻登记法实体类
 */
@AllArgsConstructor
@Data
@NoArgsConstructor

public class MarriageRegistrationEntity extends LegalEntity{
    private final String lawName = "婚姻登记条例";
    private final String info = "2003年8月8日中华人民共和国国务院令第387号公布　根据2024年12月6日《国务院关于修改和废止部分行政法规的决定》第一次修订　2025年4月6日中华人民共和国国务院令第804号第二次修订";
    private List<MarriageRegistrationRecordEntity> records;

    @Override
    public String toString() {
        return "MarriageRegistrationEntity{" +
                "lawName='" + lawName + '\'' +
                ", info='" + info + '\'' +
                ", records=" + records +
                '}';
    }
}
