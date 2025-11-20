package io.mlqs.es.legal.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 婚姻法法条实体类
 */
@Data
@Document(indexName = "bas_legal_marriage_law", createIndex = true)
public class MarriageLawRecordEntity extends LegalRecordEntity{
    /**
     * 章：第XX章
     */
    @Field(type = FieldType.Keyword)
    private String chapter;

    /**
     * 章名
     */
    @Field(type = FieldType.Keyword)
    private String chapterName;

    /**
     * 条：第XX条
     */
    @Id
    @Field(type = FieldType.Keyword)
    private String item;

    /**
     * 内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    /**
     * 向量化
     */
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] vector;
}
