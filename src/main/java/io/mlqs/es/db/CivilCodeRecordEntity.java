package io.mlqs.es.db;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

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
    @Field(type = FieldType.Keyword)
    private String code;

    /**
     *章节：第XX章
     */
    @Field(type = FieldType.Keyword)
    private String chapter;

    /**
     * 节号：第XX节
     */
    @Field(type = FieldType.Keyword)
    private String section;

    /**
     * 标题
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;

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

    //以json输出
    @Override
    public String toString(){
        return "{\"code\":\"" + code + "\",\"chapter\":\"" + chapter + "\",\"section\":\"" + section + "\",\"title\":\"" + title + "\",\"item\":\"" + item + "\",\"content\":\"" + content + "\"}";
    }
}
