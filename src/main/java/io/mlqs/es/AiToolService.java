package io.mlqs.es;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import io.mlqs.es.entity.DocumentSearchResultEntity;

import java.util.Map;

public interface AiToolService {
    @Tool("法律条文搜索工具，根据用户问题搜索相关的法律法规条文。参数context是查询的关键词或短句，参数legalName是一个字典，键是要搜索的法律文件名称，值是法条关联度阈值，只有高于阈值的搜索结果才会被返回")
    DocumentSearchResultEntity search(@ToolMemoryId String sessionId, @P("查询的关键词或短句") String context,@P("要搜索的法律文件名称与其关联度阈值") Map<String, Double> legalName);
}
