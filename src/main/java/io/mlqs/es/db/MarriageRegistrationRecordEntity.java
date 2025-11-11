package io.mlqs.es.db;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "bas_legal_marriage_registration", createIndex = true)
public class MarriageRegistrationRecordEntity extends LegalRegulationsEntity{
    /**
     *章节：第XX章
     */
    @Field(type = FieldType.Keyword)
    private String chapter;

    /**
     * 章名
     */
    @Field(type = FieldType.Keyword)
    private String chapterName;


    /**
     * 条号：第XX条
     */
    @Field(type = FieldType.Keyword)
    @Id
    private String item;

    /**
     *  内容
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;

    /**
     * 向量化
     */
    @Field(type = FieldType.Dense_Vector, dims = 1024)
    private float[] vector;

    @Override
    public String toString(){
        return "{\"chapter\":\"" + chapter + "\",\"chapterName\":\"" + chapterName + "\",\"item\":\"" + item + "\",\"content\":\"" + content + "\"}" ;
    }
}
