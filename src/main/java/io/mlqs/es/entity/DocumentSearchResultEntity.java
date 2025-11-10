package io.mlqs.es.entity;

import io.mlqs.es.entity.legal.LegalEntity;
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

    public static DocumentSearchResultEntity build(){
        return new DocumentSearchResultEntity(new HashMap<>(), new ArrayList<>());
    }

    public DocumentSearchResultEntity put(String key, LegalEntity value){
        this.content.put(key, value);
        this.docNames.add(key);
        return this;
    }
}
