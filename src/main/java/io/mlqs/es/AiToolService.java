package io.mlqs.es;

import dev.langchain4j.agent.tool.Tool;
import io.mlqs.es.entity.DocumentSearchResultEntity;

import java.util.List;

public interface AiToolService {
    @Tool("法律条文搜索工具，根据用户问题搜索相关的法律法规条文。参数context是查询的关键词或短句，参数legalName是要搜索的法律文件名称列表")
    DocumentSearchResultEntity search(String context, List<String> legalName);
}
