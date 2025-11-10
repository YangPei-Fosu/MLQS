package io.mlqs.es.db;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * 民法典法条实体类
 * 这个类用于保存民法典的记录，记录的格式为：
 */

@Data
@Document(indexName = "bas_legal_civil_code", createIndex = true)
public class CivilCodeRecordEntity extends LegalRegulationsEntity{
    /**
     * 编号：第XX编
     */
    private String id;

    /**
     *章节：第XX章
     */
    private String chapter;

    /**
     * 节号：第XX节
     */
    private String section;

    /**
     * 标题
     */
    private String title;

    /**
     * 条号：第XX条
     */
    private String item;

    /**
     *  内容
     */
    private String content;

    /**
     * 向量化
     */
    private float[] vector;

    //以json输出
    @Override
    public String toString(){
        return "{\"id\":\"" + id + "\",\"chapter\":\"" + chapter + "\",\"section\":\"" + section + "\",\"title\":\"" + title + "\",\"item\":\"" + item + "\",\"content\":\"" + content + "\"}";
    }
}
