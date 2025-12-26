package io.mlqs.es.entity;

import com.google.gson.Gson;
import io.mlqs.es.cases.db.CaseEntity;
import io.mlqs.es.legal.entity.LegalEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文档搜索结果实体类，包装搜索结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSearchResultEntity {
    /**
     * 存储的格式应该是【法律文档名，搜索结果】
     * 搜索结果将会是一个各自对应的实体的列表
     */
    private Map<String, LegalEntity> content;

    /**
     * 查询涉及的法律文档名列表
     */
    private List<String> docNames;

    /**
     * 可能有关的案件
     */
    List<CaseEntity> caseList;

    public static DocumentSearchResultEntity build(){
        return new DocumentSearchResultEntity(new HashMap<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 添加一个搜索结果
     * @param key 法律文件名
     * @param value 法律文件的实体
     * @return DocumentSearchResultEntity
     */
    public DocumentSearchResultEntity put(String key, LegalEntity value){
        this.content.put(key, value);
        this.docNames.add(key);
        return this;
    }

    public DocumentSearchResultEntity put(CaseEntity caseEntity){
        this.caseList.add(caseEntity);
        return this;
    }

    public DocumentSearchResultEntity put(List<CaseEntity> caseEntities){
        this.caseList.addAll(caseEntities);
        return this;
    }

    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
